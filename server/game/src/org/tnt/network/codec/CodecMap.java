package org.tnt.network.codec;
import gnu.trove.map.hash.TIntObjectHashMap;

public class CodecMap implements ICodecMap
{
	private final TIntObjectHashMap <ICodec<?>> codecs;



	public CodecMap ()
	{
		this.codecs = new TIntObjectHashMap <ICodec<?>> ();
	}



	@Override
	public <E> ICodec <E>  getCodec(final int id)
	{

		if(!codecs.contains( id ))
			throw new IllegalArgumentException( "Cannot find serializer for id " + id );

		return (ICodec <E>)codecs.get( id );
	}
}
