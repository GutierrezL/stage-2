import java.util.ArrayList;

public class TableList {

	private ArrayList<Table> tables;
	
	public TableList() {
		this.tables = new ArrayList<Table> ();
	}

	public void add(Table t) {
		tables.add(t);
	}
	
	public Table find(int id) {
		for (Table t : tables) {
			if (t.getTableId() == id){
				return t;
			}
		}
		return null;
	}
	
	public Table get(int i) {
		return tables.get(i);
	}
	
	
	public int getSize() {
		return tables.size();
	}
}
