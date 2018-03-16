package cc.cmu.edu.utils;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Import CSV file(s) from HDFS to HBase table.
 *
 * Please note:
 * This is a simplified tool to demonstrate how to write MapReduce jobs to
 * load data into HBase.
 *
 * This tool is not compliant with the RFC 4180 standard, and quoted fields that
 * contain commas are not allowed.
 * e.g. col1,"co,l2",col3
 *
 * This tool only supports the simplest CSVs without any quoting.
 * e.g. col,col2,col3
 *
 * Usage:
 * {@code hadoop jar import_csv.jar cc.cmu.edu.utils.YetAnotherImportCsv args}
 *
 * args example:
 * /input a.b.c.d followers data
 *
 * Before you run this program, you should:
 * 1. Make sure that lines in CSV file(s) are in the following format:
 * ROW_KEY,value1(,value2,...,valueN)
 * 2. Put the CSV file(s) into HDFS
 * e.g. hadoop fs -mkdir /input
 * hadoop fs -put data.csv /input
 * 3. Create the HBase table, along with a ColFamily
 * e.g. hbase shell
 * create 'my_table', 'data'
 */
public class YetAnotherImportCsv {

    /**
     * The mapper to tokenize the csv input with at least two columns per line.
     *
     * Input format: col1,col2(,...,colN)
     * Output format:
     * K: col1 V: col2
     * ...
     * K: col1 V: colN
     */
    private static class CsvTokenizerMapper
            extends Mapper<Object, Text, Text, Text> {

        /**
         * Logger.
         */
        private static final Logger LOGGER = Logger.getLogger(CsvTokenizerMapper.class);
        /**
         * Output K,V.
         */
        private Text outputKey = new Text(), outputValue = new Text();

        /**
         * Called once at the beginning of the task.
         */
        @Override
        protected void setup(Context context
        ) throws IOException, InterruptedException {
            LOGGER.setLevel(Level.WARN);
        }

        /**
         * Called once for each key/value pair in the input split.
         */
        @Override
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String[] columns = value.toString().split(",");
            if (columns.length == 1) {
                LOGGER.warn(String.format("Malformed CSV record: %s", value));
            } else {
                outputKey.set(columns[0]);
                for (int i = 1; i < columns.length; i++) {
                    outputValue.set(columns[i]);
                    context.write(outputKey, outputValue);
                }

            }
        }
    }

    /**
     * The reducer to read from intermediate KV pairs and write to HBase table.
     */
    private static class HBaseTableReducer extends
            TableReducer<Text, Text, ImmutableBytesWritable> {

        /**
         * This method is called once for each key.
         *
         * Input: intermediate KV pairs (K: col1 V:col2)
         * Output: write to the table:
         * ROW_KEY: col1
         * ColFamily: conf.get("COLUMN_FAMILY")
         * ColQualifer: col2
         * Cell Value: null
         */
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context
        ) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            final byte[] CF = Bytes.toBytes (conf.get("COLUMN_FAMILY"));
            Put put = new Put(Bytes.toBytes(key.toString()));
            for (Text value : values) {
                put.addColumn(CF, Bytes.toBytes(value.toString()), null);
            }
            context.write(null, put);
        }
    }

    /**
     * Main entry.
     *
     * @param args run args
     *             args[0]: HDFS input path, e.g. /input
     *             the path must be a directory, not a file
     *             args[1]: ZooKeeper address
     *             i.e. private ip of the HBase master node
     *             args[2]: HBase table name, e.g. followers
     *             args[3]: ColFamily, e.g. data
     * @throws Exception when IO error occurs
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        String zkAddr = args[1];
        conf.set("hbase.master", zkAddr + ":14000");
        conf.set("hbase.zookeeper.quorum", zkAddr);
        conf.set("hbase.zookeeper.property.clientport", "2181");
        conf.set("COLUMN_FAMILY", args[3]);
        Job job = Job.getInstance(conf, "hbase import csv");
        job.setJarByClass(YetAnotherImportCsv.class);
        job.setMapperClass(CsvTokenizerMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        TableMapReduceUtil.initTableReducerJob(
                args[2], // output table name
                HBaseTableReducer.class, // reducer class
                job);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}