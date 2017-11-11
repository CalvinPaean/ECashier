package cuny.hackthon.model;

import javax.sql.DataSource;

import cuny.hackthon.model.Models.OrderProduct;

public class OrderProductDAO extends AbstractDAO<OrderProduct, Integer>{

	public OrderProductDAO(DataSource ds) {
		super(ds);
	}

}
