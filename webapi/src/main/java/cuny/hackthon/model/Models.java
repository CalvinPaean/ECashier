package cuny.hackthon.model;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import cuny.hackthon.datautils.AutoValueGenerator.RandomDoubleSuplier;
import cuny.hackthon.datautils.AutoValueGenerator.TimestampSuplier;
import cuny.hackthon.datautils.Column;
import cuny.hackthon.datautils.Table;

public class Models {

	@Table("product_t")
	public static class Product implements IntegerKeyDataObject {

		private int id;
		@Column
		private String name;
		@Column(autoVaule=RandomDoubleSuplier.class)
		private double unitPrice;
		@Column
		private String code;
		private String qrCode;
		@Column(autoVaule=TimestampSuplier.class)
		private long lastModify;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(double unitPrice) {
			this.unitPrice = unitPrice;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getQrCode() {
			return qrCode;
		}
		public void setQrCode(String qrCode) {
			this.qrCode = qrCode;
		}
		public long getLastModify() {
			return lastModify;
		}
		public void setLastModify(long lastModify) {
			this.lastModify = lastModify;
		}
		
	}
	
	@Table("user_t")
	public static class User implements IntegerKeyDataObject {

		private int id;
		@Column
		private String name;
		
		@Column(autoVaule=RandomDoubleSuplier.class)
		private double balance;
		
		@Column(autoVaule=TimestampSuplier.class)
		private long lastModify;
		
		private int status;
		
		private String feature;
		
		@Column
		private String photo;
		
		@Override
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getBalance() {
			return balance;
		}
		public void setBalance(double balance) {
			this.balance = balance;
		}
		public long getLastModify() {
			return lastModify;
		}
		public void setLastModify(long lastModify) {
			this.lastModify = lastModify;
		}
		public String getFeature() {
			return feature;
		}
		public void setFeature(String feature) {
			this.feature = feature;
		}
		
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
		public String getPhoto() {
			return photo;
		}
		
		public void setPhoto(String photo) {
			this.photo = photo;
		}
		
		@Override
		public String toString() {
			return MessageFormat.format("id:{0}\tname:{1}\tbalance:{2}\tstatus:{3}\tlastModify:{4}", 
										id, name, balance, status,
										Instant.ofEpochMilli(lastModify).atZone(ZoneId.systemDefault())
											.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH::mm")));
		}
	}
	
	@Table("orderproduct_t")
	public static class OrderProduct implements IntegerKeyDataObject {
		
		private int id;
		
		@Column
		private int productId;
		
		@Column
		private int quantity;
		
		@Column
		private String journalId;

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public int getProductId() {
			return productId;
		}

		public void setProductId(int productId) {
			this.productId = productId;
		}

		public String getJournalId() {
			return journalId;
		}

		public void setJournalId(String journalId) {
			this.journalId = journalId;
		}
	}
	
	@Table("journal_t")
	public static class Journal implements IntegerKeyDataObject {

		private int id;
		
		@Column(autoVaule=TimestampSuplier.class)
		private long ts;
		@Column
		private int  logType;
		@Column
		private int  userId;
		@Column
		private String userFeature;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public long getTs() {
			return ts;
		}
		public void setTs(long ts) {
			this.ts = ts;
		}
		public int getLogType() {
			return logType;
		}
		public void setLogType(int logType) {
			this.logType = logType;
		}
		public int getUserId() {
			return userId;
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
		public String getUserFeature() {
			return userFeature;
		}
		public void setUserFeature(String userFeature) {
			this.userFeature = userFeature;
		}
		
	}
}
