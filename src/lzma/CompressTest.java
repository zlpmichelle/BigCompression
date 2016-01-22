package lzma;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.util.ReflectionUtils;

public class CompressTest {

  private static final Log LOG = LogFactory.getLog(CompressTest.class);

  public static void main(String[] args) throws Exception {
    System.out.println("------------Start time:" + System.currentTimeMillis());
    String codecClassname = "org.apache.hadoop.io.compress.LzmaCodec";
    Class codecClass = Class.forName(codecClassname);

    Configuration conf = new Configuration();
    CompressionCodec codec = (CompressionCodec) ReflectionUtils.newInstance(
        codecClass, conf);
    LOG.info("Created a Codec object of type: " + codecClass);

    String fileinName = args[0];
    LOG.info("Input file:" + fileinName);
    String fileoutName = fileinName + codec.getDefaultExtension();
    LOG.info("Output file:" + fileoutName);

    FileOutputStream fileout = new FileOutputStream(fileoutName);
    CompressionOutputStream out = codec.createOutputStream(fileout);
    FileInputStream filein = new FileInputStream(fileinName);
    IOUtils.copyBytes(filein, out, 4096, false);
    LOG.info("Successful Finished Compressing data.");

    out.finish();
    out.close();
    filein.close();
    System.out.println("------------End time:" + System.currentTimeMillis());
  }
}
