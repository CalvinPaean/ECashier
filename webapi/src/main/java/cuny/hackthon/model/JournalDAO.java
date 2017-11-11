package cuny.hackthon.model;

import javax.sql.DataSource;

import cuny.hackthon.model.Models.Journal;

public class JournalDAO extends AbstractDAO<Journal, Integer> {

	public JournalDAO(DataSource ds) {
		super(ds);
	}

}
