package cuny.hackthon.webapi;

public final class WebAPI {

	public final static class Path {
		public static final String index = "/";
		public static final String checkin = "/checkin/";
		public static final String checkout = "/checkout/";
		public static final String fetchUser = "/user/:id/";
		public static final String CHECK_OUT_ITEM = "/checkout/item/:code";
		public static final String SHOW_ONE_ITEM = "/item/:code";
		public static final String SHOW_QRCODE = "/product/qrcode";
		public static final String fetchAllItems = "/product/";
		public static final String newItem = "/product/";
		public static final String modifyItem = "/product/";
	}
	
	public final static class AuthPath {
		public static final String newUser = "/auth/user/new/";
		public static final String userFeature= "/auth/user/:id/feature/";
		public static final String VERIFY_USER = "/auth/user/";
	}

}
