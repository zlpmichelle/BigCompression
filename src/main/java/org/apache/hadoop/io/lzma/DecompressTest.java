package org.apache.hadoop.io.lzma;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.ReflectionUtils;

public class DecompressTest {

  private static final Log LOG = LogFactory.getLog(DecompressTest.class);

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
    String fileoutName = CompressionCodecFactory.removeSuffix(fileinName,
        codec.getDefaultExtension());
    LOG.info("Output file:" + fileoutName);

    InputStream input = null;
    OutputStream fileout = null;
    try {
      FileInputStream filein = new FileInputStream(fileinName);
      input = codec.createInputStream(filein, CodecPool.getDecompressor(codec));
      fileout = new FileOutputStream(fileoutName);
      IOUtils.copyBytes(input, fileout, 4096, false);
      LOG.info("Successful Finished Decompressing data.");
    } finally {
      IOUtils.closeStream(input);
      IOUtils.closeStream(fileout);
    }
    System.out.println("------------End time:" + System.currentTimeMillis());
  }
}
