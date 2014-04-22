package org.tnt.network.codec;

import io.netty.buffer.ByteBuf;

public interface ICodec <E> {

	public void read(ByteBuf buffer);

	public void write(E e);
}
