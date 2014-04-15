package org.tnt;



public class Halls implements IHalls
{

	private final IHall [] halls;

	public Halls()
	{
		this.halls = new IHall[255];
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public IHall getHall(final int hid) {
		return halls[hid];
	}

}
