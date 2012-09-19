package org.moparscape.msc.ls.codec;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Provides access to the protocol encoders and decoders for the LS protocol.
 */
public class LSCodecFactory implements ProtocolCodecFactory {
    /**
     * The protocol decoder in use
     */
    private static ProtocolDecoder decoder = new LSProtocolDecoder();
    /**
     * The protocol encoder in use
     */
    private static ProtocolEncoder encoder = new LSProtocolEncoder();

    /**
     * Provides the decoder to use to format outgoing data.
     * 
     * @return A protocol decoder
     */
    public ProtocolDecoder getDecoder() {
	return decoder;
    }

    /**
     * Provides the encoder to use to parse incoming data.
     * 
     * @return A protocol encoder
     */
    public ProtocolEncoder getEncoder() {
	return encoder;
    }
}
