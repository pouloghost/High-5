package gt.high5.database.table;

import gt.high5.database.model.ClassUtils;
import gt.high5.database.model.RecordTable;
import gt.high5.database.model.Table;
import gt.high5.database.model.TableAnnotation;
import gt.high5.database.model.TableUtils;

/**
 * @author GT
 * 
 *         ignores the packages in database when recording
 */
public class Ignore extends Table {

	@TableAnnotation(defaultValue = "-1")
	private int id = -1;
	@TableAnnotation(defaultValue = "")
	private String name = "";// package

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCreator() {
		return TableUtils.buildCreator(this.getClass(), Table.class);
	}

	@Override
	public String C() {
		String sql = null;
		try {
			sql = TableUtils.C(this, Table.class);
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String R() {
		String sql = null;
		try {
			sql = TableUtils.R(this, Table.class);
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String U(Table select) {
		String sql = null;
		try {
			sql = TableUtils.U(select, this, Table.class);
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public String D() {
		String sql = null;
		try {
			sql = TableUtils.D(this, Table.class);
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		}
		return sql;
	}

	@Override
	public RecordTable clone() {
		RecordTable result = null;
		try {
			result = (RecordTable) ClassUtils.clone(this, Table.class);
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

}
