package cuny.hackthon.model;

public interface DataObject<PK> {
	PK getId();
	void setId(PK id);
	
	PK convert(Object obj);
}
