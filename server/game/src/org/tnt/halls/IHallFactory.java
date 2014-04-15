package org.tnt.halls;

import org.tnt.IHall;

public interface IHallFactory {

	IHall getHall(int hallId);

}
