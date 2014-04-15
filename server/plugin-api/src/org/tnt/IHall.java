package org.tnt;

import io.netty.buffer.ByteBuf;

public interface IHall {

	public void read(final ByteBuf buffer);

}
