/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.io.erasurecode.coder.ErasureCoder;
import org.apache.hadoop.io.erasurecode.coder.RSErasureEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RSRawDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RSRawEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureCoderFactory;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.XORRawDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.XORRawEncoder;

/**
 * A codec & coder utility to help create raw coders conveniently.
 */
@InterfaceAudience.Private
public final class CodecUtil {

  private CodecUtil() { }

  /**
   * Create RS raw encoder according to configuration.
   * @param conf configuration possibly with some items to configure the coder
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   * @return raw encoder
   */
  public static RawErasureEncoder createRSRawEncoder(
      Configuration conf, int numDataUnits, int numParityUnits) {
    RawErasureCoder rawCoder = createRawCoder(conf,
        CommonConfigurationKeys.IO_ERASURECODE_CODEC_RS_RAWCODER_KEY,
        true, numDataUnits, numParityUnits);
    if (rawCoder == null) {
      rawCoder = new RSRawEncoder(numDataUnits, numParityUnits);
    }

    return (RawErasureEncoder) rawCoder;
  }

  /**
   * Create RS raw decoder according to configuration.
   * @param conf configuration possibly with some items to configure the coder
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   * @return raw decoder
   */
  public static RawErasureDecoder createRSRawDecoder(
      Configuration conf, int numDataUnits, int numParityUnits) {
    RawErasureCoder rawCoder = createRawCoder(conf,
        CommonConfigurationKeys.IO_ERASURECODE_CODEC_RS_RAWCODER_KEY,
        false, numDataUnits, numParityUnits);
    if (rawCoder == null) {
      rawCoder = new RSRawDecoder(numDataUnits, numParityUnits);
    }

    return (RawErasureDecoder) rawCoder;
  }

  /**
   * Create XOR raw encoder according to configuration.
   * @param conf configuration possibly with some items to configure the coder
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   * @return raw encoder
   */
  public static RawErasureEncoder createXORRawEncoder(
      Configuration conf, int numDataUnits, int numParityUnits) {
    RawErasureCoder rawCoder = createRawCoder(conf,
        CommonConfigurationKeys.IO_ERASURECODE_CODEC_XOR_RAWCODER_KEY,
        true, numDataUnits, numParityUnits);
    if (rawCoder == null) {
      rawCoder = new XORRawEncoder(numDataUnits, numParityUnits);
    }

    return (RawErasureEncoder) rawCoder;
  }

  /**
   * Create XOR raw decoder according to configuration.
   * @param conf configuration possibly with some items to configure the coder
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   * @return raw decoder
   */
  public static RawErasureDecoder createXORRawDecoder(
      Configuration conf, int numDataUnits, int numParityUnits) {
    RawErasureCoder rawCoder = createRawCoder(conf,
        CommonConfigurationKeys.IO_ERASURECODE_CODEC_XOR_RAWCODER_KEY,
        false, numDataUnits, numParityUnits);
    if (rawCoder == null) {
      rawCoder = new XORRawDecoder(numDataUnits, numParityUnits);
    }

    return (RawErasureDecoder) rawCoder;
  }

  /**
   * Create raw coder using specified conf and raw coder factory key.
   * @param conf configuration possibly with some items to configure the coder
   * @param rawCoderFactoryKey configuration key to find the raw coder factory
   * @param isEncoder is encoder or not we're going to create
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   * @return raw coder
   */
  public static RawErasureCoder createRawCoder(Configuration conf,
      String rawCoderFactoryKey, boolean isEncoder, int numDataUnits,
                                               int numParityUnits) {

    if (conf == null) {
      return null;
    }

    Class<? extends RawErasureCoderFactory> factClass = null;
    factClass = conf.getClass(rawCoderFactoryKey,
        factClass, RawErasureCoderFactory.class);

    if (factClass == null) {
      return null;
    }

    RawErasureCoderFactory fact;
    try {
      fact = factClass.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException("Failed to create raw coder", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Failed to create raw coder", e);
    }

    return isEncoder ? fact.createEncoder(numDataUnits, numParityUnits) :
            fact.createDecoder(numDataUnits, numParityUnits);
  }

  /**
   * Create an {@link ErasureCoder} to encode a block group.
   * @param numDataUnits number of data units in a coding group
   * @param numParityUnits number of parity units in a coding group
   */
  public static ErasureCoder createErasureEncoder(Configuration conf,
      int numDataUnits, int numParityUnits) {
    return new RSErasureEncoder(numDataUnits, numParityUnits);
  }
}
