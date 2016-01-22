package org.apache.hadoop.io.orc.test;

import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.orc.OrcOutputFormat;
import org.apache.hadoop.io.orc.OrcSerde;
import org.apache.hadoop.io.orc.OrcSerde.OrcSerdeRow;
import org.apache.hadoop.io.orc.OrcStruct;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class OrcTransition {

  public static class OrcMapper extends
      Mapper<LongWritable, Text, NullWritable, OrcSerdeRow> {
    // real value of column fields
    private String[] fieldDataByLine;
    // orcSerde
    private OrcSerde orcSerde;
    private Properties property;

    public void map(Object key, Text line, Context context) throws IOException,
        InterruptedException, SerDeException {

      // parse text value line by line
      fieldDataByLine = line.toString().split("#");

      orcSerde = new OrcSerde();
      Properties property = new Properties();
      String colNames = "c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,"
          + "c21,c22,c23,c24,c25,c26,c27,c28,c29,c30,c31,c32,c33,c34,c35,c36,c37,c38,c39,c40,"
          + "c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55";
      property.setProperty("columns", colNames);

      orcSerde.initialize(null, property);

      OrcStruct st = new OrcStruct(55);
      st.setNumFields(55);
      for (int i = 0; i < 55; i++) {
        st.setFieldValue(i, fieldDataByLine[i]);
      }
      context.write(NullWritable.get(),
          (OrcSerdeRow) orcSerde.serialize(st, orcSerde.getObjectInspector()));
    }

    public static void main(String[] args) throws Exception {
      Configuration conf = new Configuration();
      String[] otherArgs = new GenericOptionsParser(conf, args)
          .getRemainingArgs();
      if (otherArgs.length != 2) {
        System.err.println("Usage: orctansition <in> <out>");
        System.exit(2);
      }
      Job job = new Job(conf, "Orc File transition");
      job.setJarByClass(OrcTransition.class);
      job.setMapperClass(OrcMapper.class);
      // job.setCombinerClass(IntSumReducer.class);
      // job.setReducerClass(IntSumReducer.class);
      job.setOutputKeyClass(Text.class);
      job.setOutputValueClass(OrcSerde.class);
      FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
      OrcOutputFormat.setOutputPath((JobConf) conf, new Path(otherArgs[1]));
      OrcOutputFormat.setCompressOutput((JobConf) conf, true);
      System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
  }
}
