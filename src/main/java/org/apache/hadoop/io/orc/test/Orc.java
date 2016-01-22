package org.apache.hadoop.io.orc.test;

import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.orc.OrcOutputFormat;
import org.apache.hadoop.io.orc.OrcSerde;
import org.apache.hadoop.io.orc.OrcSerde.OrcSerdeRow;
import org.apache.hadoop.io.orc.OrcStruct;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Orc {

  public static class OrcMapper extends MapReduceBase implements
      Mapper<LongWritable, Text, NullWritable, OrcSerdeRow> {
    // real value of column fields
    private String[] fieldDataByLine;
    // orcSerde
    private OrcSerde orcSerde;

    public void map(LongWritable key, Text line,
        OutputCollector<NullWritable, OrcSerdeRow> output, Reporter reporter)
        throws IOException {

      fieldDataByLine = line.toString().split(",");

      orcSerde = new OrcSerde();
      Properties property = new Properties();
      String colNames = "c1,c2,c3,c4,c5,c6,c7,c8,c9,c10,c11,c12,c13,c14,c15,c16,c17,c18,c19,c20,c21,c22,c23,c24,c25,c26,c27,c28,c29,c30,c31,c32,c33,c34,c35,c36,c37,c38,c39,c40,c41,c42,c43,c44,c45,c46,c47,c48,c49,c50,c51,c52,c53,c54,c55,c56,c57,c58,c59,c60,c61,c62,c63,c64,c65,c66,c67,c68,c69,c70,c71,c72,c73,c74,c75,c76,c77,c78,c79,c80,c81,c82,c83,c84,c85,c86,c87,c88,c89,c90,c91,c92,c93,c94,c95,c96,c97,c98,c99,c100,c101,c102,c103,c104,c105,c106,c107,c108,c109,c110,c111,c112";
      property.setProperty("columns", colNames);

      orcSerde.initialize(null, property);

      OrcStruct st = new OrcStruct(112);
      st.setNumFields(112);
      for (int i = 0; i < 112; i++) {
        st.setFieldValue(i, new Text(fieldDataByLine[i]));
      }

      try {
        output
            .collect(
                NullWritable.get(),
                (OrcSerdeRow) orcSerde.serialize(st,
                    orcSerde.getObjectInspector()));
      } catch (SerDeException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    JobConf conf = new JobConf(Orc.class);

    String[] otherArgs = new GenericOptionsParser(conf, args)
        .getRemainingArgs();
    if (otherArgs.length != 2) {
      System.err.println("Usage: wordcount <in> <out>");
      System.exit(2);
    }

    FileInputFormat.setInputPaths(conf, new Path(otherArgs[0]));
    OrcOutputFormat.setOutputPath(conf, new Path(otherArgs[1]));

    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(OrcOutputFormat.class);

    conf.setOutputKeyClass(NullWritable.class);
    conf.setOutputValueClass(OrcSerdeRow.class);

    conf.setNumReduceTasks(0);
    conf.setMapperClass(OrcMapper.class);

    JobClient.runJob(conf);
  }
}
