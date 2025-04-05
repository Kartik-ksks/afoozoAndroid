package in.kpis.afoozo.util;

/**
 * Created by Nss Solutions on 16-11-2016.
 */

public class Constants {

    public static final String CAFE = "Cafe" ;
    public static final String ROOM_BOOKING = "RoomBooking";
    public static final String BILL_TO_COMPANY = "Bill To Company";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String PAYMENT_URL = "paymentUrl";

    public static String MERCHANT_KEY = "H0ZzI7du"; // Test
    public static String MERCHANT_ID = "7083644";

    public static final String IS_SAVED_CARD = "isSavedCard";
    public static final String PLACE_ORDER_JSON = "placeorderjson";
    public static final String NICK_NAME = "nick_name";
    public static final String NICKNAME = "nickName";
    public static final String TITLE = "title";
    public static final String FLD_SPECIAL_INSTRUCTION = "specialInstruction";
    public static String RESPONSE = "response";

    public static final String REAL_TIME = "REAL_TIME";
    public static final String SCHEDULE = "Schedule";
    /**
     * Different kind of user rolls
     */

    /* Cash Free*/
/*    public static String CASH_FREE_MID = "799012583642b079cd197b7cc10997";  // Test
    public static final String PAYMENT_GATEWAY_TYPE = "TEST";*/
    public static String CASH_FREE_MID = "1140874f1ef94a6098e6e59bcd780411";  // Live
    public static final String PAYMENT_GATEWAY_TYPE = "PROD";

    public static String LOCAL_INTENT_STATUS_CHANGE = "statusChanged";

    public static String PAYTM_MID = "LAPYRA39397814135444";  // Live
//    public static String PAYTM_MID = "LAPYRA18951497688831"; // Testing

    public static String FLD_SCREEN_WIDTH = "screen_w";

    public static String VAL_CONTENT_TYPE = "application/json";



    public static String SERVER_DATE_FORMAT_FOR_SENDING = "yyyy-MM-dd";
    public static String SERVER_DATE_FORMAT_COMING = "dd/MMM/yyyy";
    public static String DATE_TIME_FORMAT_FOR_SHOWING = "dd MMM,yyyy hh:mm a";
    public static String DATE_FORMAT_FOR_SHOWING = "dd MMM, yyyy";

    //common field constants
    public static String FLD_RESPONSE = "response";
    public static String FLD_ERROR_MSG = "message";

    //common constants values
    public static String VAL_SUCCESS = "Success";
    public static String VAL_UNKNOWN = "server not responding";

    //Request types
    public static String VAL_POST = "post";
    public static String VAL_GET = "get";
    public static String VAL_DELETE = "delete";

    //Request headers
    public static String FLD_CONTENT_TYPE = "Content-Type";
    public static String FLD_CIPHER = "cipher";
    public static String FLD_BUILD_VERSION = "buildVersion";
    public static String FLD_DEVICE_TYPE = "deviceType";


    // Login Parameters

    public static String FLD_USER_MODEL_JSON = "userModelJson";

    public static String FLD_NOTIFICATION_JSON = "notificationJson";

    public static String FLD_SCAN_BEAN_JSON = "scanBeanJson";
    public static String FLD_CAFE_SCAN_BEAN_JSON = "scanCafeBeanJson";

    public static String FLD_USER_TOKEN = "userToken";

    public static String FLD_STATUS = "status";

    public static String FLD_DEVICE_TOKEN = "fcmDeviceToken";

    public static String FLD_USER_ID = "user_id";


    public static String FLD_IS_LOGIN = "isLogin";
    public static String FLD_IS_TOKEN_UPDATED = "isTokenUpdated";

    public static final int DEFAULT_ID = 0;

    public static String FLD_EMAIL = "email";
    public static String FLD_USER_NAME = "username";


    public static String FLD_CENTER_ID = "center_id";

    public static String FLD_LAT = "lat";
    public static String FLD_LONG = "long";
    public static String FLD_CART_COUNT = "cartCount";
    public static String FLD_IS_VARIFIED = "isVarified";
    public static String FLD_IS_SKIPPED = "isSkipped";

    public static String FROM_WHICH = "fromWhich";
    public static String MOBILE_NO = "mobileNo";
    public static String DELIVERY_TIME = "deliveryTime";
    public static String FLD_BASIC_AUTH = "basicAuth";
    public static String FLD_MOBILE_NUMBER = "mobileNumber";
    public static String FLD_ONETIME_PASSWORD = "oneTimePassword";
    public static String FLD_DELIVERY_OFFER_JSON = "awsDeliveryOfferJson";
    public static String IS_FROM_REGISTER = "isFromRegister";
    public static String IS_FOR_MOBILE_LOGIN = "isForMobileLogin";
    public static String FLD_IS_PROFILE_UPDATE = "isProfileUpdate";
    public static String DASHBOARD_TOP = "DashboardTop";
    public static String DASHBOARD_BOTTOM = "DashboardBottom";
    public static String DELIVERY  = "Delivery";
    public static String SHORTBANNER = "ShortBanner";
    public static String VOUCHER_CODE = "voucherCode";
    public static String RESTAURANT_DATA = "restaurantData";
    public static String RESTAURANT_ID = "restaurantId";
    public static String ORDER_ID = "orderId";
    public static String IS_FROM_CART = "isFromCart";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String ADDRESS = "address";
    public static String FLAT_NO = "flatNo";
    public static String    TOTAL_AMOUNT = "totalAmount";
    public static String ADDRESS_ID = "addressId";
    public static String ORDER_REFERENCE_ID = "orderReferenceId";
    public static String COUPON_CODE = "couponCode";
    public static String DISCOUNT = "discount";
    public static String PAYMENT_MODE = "paymentMode";
    public static String PAYMENT_TYPE = "paymentType";
    public static String BANK_BEAN = "bankBean";
    public static String SAVED_CARD_DATA = "savedCartData";
    public static String CARD_DATA = "cardData";
    public static String TRANSACTION_ID = "transactionId";
    public static String IS_OPEN = "isOpen";
    public static String TAKE_AWAY = "TakeAway";
    public static String HOME_DELIVERY = "HomeDelivery";
    public static String ALL = "All";
    public static String EVENT_NAME = "eventName";
    public static String SCAN_QR_BEAN = "scanQrBean";
    public static String DINE_IN = "DineIn";
    public static String STEAL_DEAL = "StealDeal";
    public static String CITY_ID = "cityId";
    public static String Is_FROM_HOME = "isFromHome";
    public static String IS_FROM_WALLET = "isFromWallet";
    public static String RESTAURANT_NAME = "restaurantName";
    public static String IS_FROM_HOME = "isFromHome";
    public static String FLD_ADDRESS_MODEL_JSON = "addressModelJson";
    public static String PAYMENT_AMOUNT = "paymentAmount";
    public static String GENERATEPAYMENTREQUESTFORORDER = "generatePaymentRequestForOrder";
    public static String PAYMENT_GATEWAY = "paymentGateway";
    public static String PAYMENT_GATEWAY_REF_ID = "paymentGatewayRefId";
    public static String PAYMENT_REQUEST_ID = "paymentRequestId";
    public static String STEAL_DEAL_ITEM_ID = "stealDealItemId";
    public static String STEAL_DEAL_ITEM_RESERVATION_ID = "stealDealItemReservationId";
    public static String QUANTITY = "quantity";
    public static String QR_CODE = "qrCode";
    public static String SLIDER_ACTION = "sliderAction";
    public static String TIP_AMOUNT = "tipAmount";
    public static String TIP_MESSAGE = "tipMessage";
    public static String ADDRESS_BEAN = "addressBean";
    public static String FLD_DATA_FOR_WHICH = "dataFroWhich";
    public static String IS_FROM_SPLASH = "isFromSplash";
    public static String ORDER_TYPE = "orderType";
    public static String IS_TIP_DONE = "isTipDone";
    public static String FLD_FACEBOOK_USER_ID = "facebookUserId";
    public static String FLD_FACEBOOK_USER_ACCESS_TOKEN = "facebookUserAccessToken";
    public static String IS_FROM_NOTIFICATION = "isFromNotification";
    public static String MESSAGE = "message";
    public static String IMAGE_URL = "imageUrl";
    public static String TAKEAWAY_MIN_ORDER_VALUE = "takeawayMinOrderValue";
    public static String DELIVERY_MIN_ORDER_VALUE = "deliveryMinOrderValue";
    public static String ORDER_TOTAL = "orderTotal";
    public static String FLD_COUPON_CODE = "couponCode";
}
