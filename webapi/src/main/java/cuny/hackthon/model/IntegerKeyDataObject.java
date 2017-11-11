package cuny.hackthon.model;

public interface IntegerKeyDataObject extends DataObject<Integer> {
	

	default @Override
	Integer convert(Object obj) {
		if(obj == null) return 0;
		return Integer.parseInt(obj.toString());
	}
}
