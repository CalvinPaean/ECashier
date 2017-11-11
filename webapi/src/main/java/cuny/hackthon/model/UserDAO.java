package cuny.hackthon.model;

import javax.sql.DataSource;

import cuny.hackthon.model.Models.User;

public class UserDAO extends AbstractDAO<User, Integer> {

	public UserDAO(DataSource ds) {
		super(ds);
	}
	
}
