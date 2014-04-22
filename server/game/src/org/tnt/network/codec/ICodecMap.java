package org.tnt.network.codec;


public interface ICodecMap
{


	public <E> ICodec <E> getCodec(int id);
}
