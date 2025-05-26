package in.kpis.afoozo.okhttpServcice;

/**
 * Created by Hemant Jangid. Update by Sohil Beg
 */

public class AppUrls {
//    public static final String BASE = "http://3.7.22.149/";//test
//    public static final String BASE = "http://192.168.1.20:8085/";
//            public static final String BASE = "http://test.afoozocafe.com/";
    public static final String BASE = "https://afoozocafe.com/";

//    public static final String BASE = "http://192.168.1.25:8085/";
    public static final String BASE_URL = BASE + "v1/api/";
    public static final String BASE_URL_PAYTM = BASE + "v1/api/paytm/";
    public static final String BASE_URL_PayUmoney = BASE + "v1/api/payUMoney/";
    public static final String BASE_URL_CASH_FREE = BASE + "v1/api/cashFree/";
    public static final String BASE_URL_STATIC = BASE + "staticPage/";
    public static final String GENERATE_OTP_FOR_USER = BASE_URL + "generateOtpForUser";
    public static int REQUESTTAG_GENERATEOTPFORUSER = 0;
    public static final String LOGIN_USER_BY_OTP = BASE_URL + "loginUserByOtp";
    public static int REQUESTTAG_LOGINUSERBYOTP = 1;
    public static final String UPDATE_PROFILE = BASE_URL + "updateProfile";
    public static int REQUESTTAG_UPDATEPROFILE = 2;
    public static final String GET_UPCOMING_LIST = BASE_URL + "getEventList/upComing/";
    public static int REQUESTTAG_GETUPCOMINGLIST = 3;
    public static final String GET_PAST_LIST = BASE_URL + "getEventList/past/";
    public static int REQUESTTAG_GETPASTLIST = 4;

    public static final String GET_EVENT_DETAIL = BASE_URL + "getEventDetail/";
    public static int REQUESTTAG_GETEVENTDETAIL = 5;

    public static final String UPLOAD_FILE = BASE_URL + "uploadFile";
    public static int REQUESTTAG_UPLOADFILE = 6;

    public static final String GET_AD_BANNER_LIST = BASE_URL + "getAdBannerList/";
    public static int REQUESTTAG_GETADBANNERLIST = 7;

    public static final String GET_BALANCE = BASE_URL + "getBalance";
    public static int REQUESTTAG_GETBALANCE = 8;

    public static final String GET_COIN_AND_WALLET_BALANCE = BASE_URL + "getCoinAndWalletBalance";
    public static int REQUESTTAG_GETCOINANDWALLETBALANCE = 8;

    public static final String REDEEM_VOUCHER = BASE_URL + "redeemVoucher";
    public static int REQUESTTAG_REDEEMVOUCHER = 9;

    public static final String RESTAURANT_LIST = BASE_URL + "restaurantList";
    public static int REQUESTTAG_RESTAURANTLIST = 10;

    public static final String GET_ITEM_LIST_WITH_CAT_SUBCAT = BASE_URL + "getItemListWithCatSubCat";
    public static int REQUESTTAG_ITEMLIST = 11;

    public static final String SAVE_ORDER = BASE_URL + "saveOrder";
    public static int REQUESTTAG_SAVEORDER = 12;

    public static final String ORDER_DETAIL = BASE_URL + "orderDetail/";
    public static int REQUESTTAG_ORDERDETAIL = 13;

    public static final String COUPON_LIST = BASE_URL + "couponList/";
    public static int REQUESTTAG_COUPONLIST = 14;

    public static final String APPLY_COUPON_CODE = BASE_URL + "applyCoupon";
    public static int REQUESTTAG_APPLYCOUPONCODE = 15;

    public static final String SAVE_ADDRESS = BASE_URL + "saveAddress";
    public static int REQUESTTAG_SAVEADDRESS = 16;

    public static final String ADDRESS_LIST = BASE_URL + "getAddressList";
    public static int REQUESTTAG_ADDRESSLIST = 17;

    public static final String UPDATE_ADDRESS = BASE_URL + "updateAddress";
    public static int REQUESTTAG_UPDATEADDRESS = 18;

    public static final String PLACE_ORDER = BASE_URL + "placeOrder";
    public static int REQUESTTAG_PLACEORDER = 19;

    public static final String CHECK_PAYMENT_STATUS = BASE_URL + "placeOrder";
    public static int REQUESTTAG_CHECKPAYMENTSTATUS = 20;

    public static final String GETLASTPAYMENTMODE = BASE_URL + "getLastPaymentMode_v1";
    public static int REQUESTTAG_GETLASTPAYMENTMODE = 21;

    public static final String GETSAVEDCARDS = BASE_URL + "savedCardList";
    public static int REQUESTTAG_GETSAVEDCARDS = 22;

    public static final String UPDATE_ORDER_ITEM_QUANTITY = BASE_URL + "updateOrderItemQuantity/";
    public static int REQUESTTAG_UPDATEORDERITEMQUANTITY = 23;

    public static final String REMOVE_COUPON = BASE_URL + "removeCoupon/";
    public static int REQUESTTAG_REMOVECOUPON = 24;

    public static final String GETORDERLIST = BASE_URL + "getOrderList/";
    public static int REQUESTTAG_GETORDERLIST = 25;

    public static final String GETDRIVERLOCATION = BASE_URL + "getDriverLocation/";
    public static int REQUESTTAG_GETDRIVERLOCATION = 26;

    public static final String RATEORDERBYCUSTOMER = BASE_URL + "rateOrderByCustomer/";
    public static int REQUESTTAG_RATEORDERBYCUSTOMER = 27;

    public static final String GETALLCITYLIST = BASE_URL + "getAllCityList";
    public static int REQUESTTAG_GETALLCITYLIST = 28;

    public static final String SAVERESERVATION = BASE_URL + "saveReservation";
    public static int REQUESTTAG_SAVERESERVATION = 30;

    public static final String SCAN_QR_CODE = BASE_URL + "scanQrCode/";
    public static int REQUESTTAG_SCANQRCODE = 32;

    public static final String SAVE_FEEDBACK = BASE_URL + "saveFeedback";
    public static int REQUESTTAG_SAVEFEEDBACK = 33;

    public static final String GET_STEAL_DEAL_CATEGORY_LIST = BASE_URL + "getStealDealCategoryList";
    public static int REQUESTTAG_GETSTEALDEALCATEGORYLIST = 35;

    public static final String PAY_AT_RESTAURANT = BASE_URL + "payAtRestaurant";
    public static int REQUESTTAG_PAYATRESTAURANT = 36;

    public static final String GENERATE_PAYMENT_REQUEST = BASE_URL + "generatePaymentRequest";
    public static int REQUESTTAG_GENERATEPAYMENTREQUEST = 37;

    public static final String UPDATE_PAYMENT_REQUEST = BASE_URL + "updatePaymentRequest/";
    public static int REQUESTTAG_UPDATEPAYMENTREQUEST = 38;

    public static final String UPDATE_DEVICE_DETAIL = BASE_URL + "updateDeviceDetail";
    public static int REQUESTTAG_UPDATEDEVICEDETAIL = 39;

    public static final String GET_STEAL_DEAL_ITEM_LIST = BASE_URL + "getStealDealItemList";
    public static int REQUESTTAG_GETSTEALDEALITEMLIST = 40;

    public static final String GET_STEAL_DEAL_ITEM_DETAIL = BASE_URL + "getStealDealItemDetail/";
    public static int REQUESTTAG_GETSTEALDEALITEMDETAIL = 41;

    public static final String RESERVE_STEAL_DEAL_ITEMS = BASE_URL + "reserveStealDealItems";
    public static int REQUESTTAG_RESERVESTEALDEALITEM = 42;

    public static final String STEAL_DEAL_RESERVATION_ITEM_ITEMLIST = BASE_URL + "stealDealReservationItemItemList";
    public static int REQUESTTAG_STEALDEALRESERVATIONITEMITEMLIST = 43;

    public static final String GENERATE_GIFT_CODE = BASE_URL + "generateGiftCode";
    public static int REQUESTTAG_GENERATEGIFTCODE = 44;

    public static final String STEAL_DEAL_ORDER = BASE_URL + "stealDealOrder";
    public static int REQUESTTAG_STEALDEALORDER = 45;

    public static final String RE_ORDER = BASE_URL + "reOrder/";
    public static int REQUESTTAG_REORDER = 46;

    public static final String PAY_TIP_ON_ORDER = BASE_URL + "payTipOnOrder/";
    public static int REQUESTTAG_PAYTIPONORDER = 47;

    public static final String OCCASION_LIST = BASE_URL + "tableReservationOccasionList";
    public static int REQUESTTAG_OCCASIONLIST = 48;

    public static final String GET_NOTIFICATION_LIST = BASE_URL + "getNotificationList/";
    public static int REQUESTTAG_GETNOTIFICATIONLIST = 49;

    public static final String APPLY_GIFT_CODE = BASE_URL + "applyGiftCode/";
    public static int REQUESTTAG_APPLYGIFTCODE = 50;

    public static final String GET_WALLET_TRANSACTION_LIST_DATA = BASE_URL + "getWalletTransactionListData/";
    public static int REQUESTTAG_GETWALLETTRANSACTIONLISTDATA = 51;

    public static final String DELETE_ADDRESS = BASE_URL + "deleteAddress/";
    public static int REQUESTTAG_DELETEADDRESS = 52;

    public static final String GET_LAST_ORDER_WHICH_WAS_NOT_RATED = BASE_URL + "getLastOrderWhichWasNotRated";
    public static int REQUESTTAG_GETLASTORDERWHICHWASNOTRATED = 53;

    public static final String PAY_TIP_ON_STEAL_DEAL_ORDER = BASE_URL + "payTipOnStealDealOrder/";
    public static int REQUESTTAG_PAYTIPONSTEALDEALORDER = 54;

    public static final String SET_LAST_PAYMENT_MODE = BASE_URL + "setLastPaymentMode";
    public static int REQUESTTAG_SETLASTPAYMENTMODE = 55;

    public static final String GET_TOP_SELLING_ITEM_LIST = BASE_URL + "getTopSellingItemList";
    public static int REQUESTTAG_GETTOPSELLINGITEMLIST = 56;

    public static final String GET_NOTIFICATION_SETTING = BASE_URL + "getNotificationSetting";
    public static int REQUESTTAG_GETNOTIFICATIONSETTING = 57;

    public static final String SAVE_NOTIFICATION_SETTING = BASE_URL + "saveNotificationSetting";
    public static int REQUESTTAG_SAVENOTIFICATIONSETTING = 58;

    public static final String RATE_STEAL_DEAL_ORDER_BY_CUSTOMER = BASE_URL + "rateStealDealOrderByCustomer/";
    public static int REQUESTTAG_RATESTEALDEALORDERBYCUSTOMER = 59;

    public static final String LOGIN_USER_BY_FACEBOOK = BASE_URL + "loginUserByFacebook";
    public static int REQUESTTAG_LOGINUSERBYFACEBOOK = 60;

    public static final String GET_VERSION = BASE_URL + "getVersion/";
    public static int REQUESTTAG_GETVERSION = 60;

    public static final String PG_REDIRECT = BASE_URL_PAYTM + "pgRedirect/";
    public static int REQUESTTAG_PGREDIRECT = 61;

    public static final String VERIFY_TRANSACTION = BASE_URL_PAYTM + "verifyTransaction/";
    public static int REQUESTTAG_VERIFYTRANSACTION = 62;

    public static final String GET_ATM_MACHINE_HISTORY_LIST_DATA = BASE_URL + "getAtmMachineHistoryListData/";
    public static int REQUESTTAG_GETATMMACHINEHISTORYLISTDATA = 63;

    public static final String GET_RFID_TRANSACTION_LIST_DATA = BASE_URL + "getRfidTransactionListData/";
    public static int REQUESTTAG_GETRFIDTRANSACTIONLISTDATA = 64;

    public static final String GET_ALL_SCRATCH_CARD = BASE_URL + "getAllScratchCard";
    public static int REQUESTTAG_GETALLSCRATCHCARD = 65;

    public static final String SCRATCH_THE_CARD = BASE_URL + "scratchTheCard/";
    public static int REQUESTTAG_SCRATCHTHECARD = 66;

    public static final String GET_DELIVERY_OFFER = BASE_URL + "getDeliveryOffer";
    public static int REQUESTTAG_GETDELIVERYOFFER = 67;

    public static final String SCAN_QR_REWARD = BASE_URL + "scanQrReward/";
    public static int REQUESTTAG_SCANQRREWARD = 68;
 
    public static final String UPDATE_INSTRUCTION_ON_ORDER_ITEM = BASE_URL + "updateInstructionOnOrderItem/";
    public static int REQUESTTAG_UPDATEINSTRUCTIONONORDERITEM = 69;

    public static final String SEND_VOICE_OTP_FOR_USER = BASE_URL + "sendVoiceOtpForUser";
    public static int REQUESTTAG_SENDVOICEOTPFORUSER = 70;

    public static final String REQUEST_FOR_TABLE_CLEANING = BASE_URL + "requestForTableCleaning/";
    public static int REQUESTTAG_REQUESTFORTABLECLEANING = 71;

    public static final String GET_QR_IMAGE_BY_TEXT = BASE_URL + "getQRImageByText/";
    public static int REQUESTTAG_GETQRIMAGEBYTEXT = 72;

    public static final String GENERATE_HASH = BASE_URL_PayUmoney + "generateHash";
    public static int REQUESTTAG_GENERATEHASH = 73;

    public static final String CUISINE_LIST = BASE_URL + "cuisineList";
    public static int REQUESTTAG_CUISINELIST = 74;

    public static final String GET_PAYMENT_GATEWAY_LIST = BASE_URL + "getPaymentGatewayList/";
    public static int REQUESTTAG_GETPAYMENTGATEWAYLIST = 75;

    public static final String SCAN_CAFE_QR_CODE = BASE_URL + "scanCafeQrCode/";
    public static int REQUESTTAG_SCANCAFEQRCODE = 76;

    public static final String GET_NEAREST_ADDRESS = BASE_URL + "getNearestAddress";
    public static int REQUESTTAG_GETNEARESTADDRESS = 77;

    public static final String APPLY_LOYALTY = BASE_URL + "applyLoyalty";
    public static int REQUESTTAG_APPLYLOYALTY = 78;

    public static final String REMOVE_LOYALTY = BASE_URL + "removeLoyalty/";
    public static int REQUESTTAG_REMOVELOYALTY = 79;

    public static final String GET_COIN_TRANSACTION_LIST_DATA = BASE_URL + "getCoinTransactionListData/";
    public static int REQUESTTAG_GETCOINTRANSACTIONLISTDATA = 80;

    public static final String GET_ROOM_AVAILABILITY = BASE_URL + "getRoomAvailability";
    public static int REQUESTTAG_GETROOMAVAILABILITY = 81;

    public static final String SAVE_ROOM_BOOKING = BASE_URL + "saveRoomBooking";
    public static int REQUESTTAG_SAVEROOMBOOKING = 82;

    public static final String SCAN_QR_CODEFOR_ROOM_BOOKING = BASE_URL + "scanQrCodeForRoomBooking/";
    public static int REQUESTTAG_SCANQRCODEFORROOMBOOKING = 83;

    public static final String ROOM_BOOKING_DETAIL = BASE_URL + "roomBookingDetail/";
    public static int REQUESTTAG_ROOMBOOKINGDETAIL = 84;

    public static final String PLACE_ROOM_BOOKING = BASE_URL + "placeRoomBooking";
    public static int REQUESTTAG_PLACEROOMBOOKING = 85;

    public static final String GENERATE_TOKEN = BASE_URL_CASH_FREE + "generateToken";
    public static int REQUESTTAG_GENERATETOKEN = 86;

    public static final String createOrder = BASE_URL_CASH_FREE + "createOrder";
    public static int REQUESTTAG_CREATE_ORDER = 86;


    public static final String verifiedPaymentCashFree = BASE_URL_CASH_FREE + "verifiedPayment/";
    public static int REQUESTTAG_verifiedPaymentCashFree = 86;




    public static final String UPDATE_PAYMENT_REQUEST_FOR_CASH_FREE = BASE_URL + "updatePaymentRequestForCashFree";
    public static int REQUESTTAG_UPDATEPAYMENTREQUESTFORCASHFREE = 87;

    public static final String UPDATE_PAYMENT_REQUEST_FOR_CASH_FREE_V2 = BASE_URL + "updatePaymentRequestForCashFreeV2";
    public static int REQUESTTAG_UPDATEPAYMENTREQUESTFORCASHFREE_V2 = 87;

    public static final String GET_ROOM_BOOKING_LIST = BASE_URL + "getRoomBookingList/";
    public static int REQUESTTAG_GETROOMBOOKINGLIST = 88;

    public static final String APPLY_COUPON_FOR_BOOKING = BASE_URL + "applyCouponForBooking";
    public static int REQUESTTAG_APPLYCOUPONFORBOOKING = 89;

    public static final String REMOVECOUPON_FOR_BOOKING = BASE_URL + "removeCouponForBooking/";
    public static int REQUESTTAG_REMOVECOUPONFORBOOKING = 90;

    public static final String GET_PAYMENTGATEWAYLIST_V1 = BASE_URL + "getPaymentGatewayList_v1/";
    public static int REQUESTTAG__GETPAYMENTGATEWAYLIST_V1 = 90;   // replace 75 api

    public static final String GET_ORDER_LIST_FOR_BILL_TO_COMPANY = BASE_URL + "getOrderListForBillToCompany";
    public static int REQUESTTAG_GET_ORDER_LIST_FOR_BILL_TO_COMPANY = 91;   // replace 75 api

    public static final String GENERATE_TRANSACTION_LINK = BASE_URL + "sodexo/generateTransactionLink";
    public static int REQUESTTAG_GENERATE_TRANSACTION_LINK = 92;

    public static final String GET_TRANSACTION_DETAIL = BASE_URL + "sodexo/getTransactionDetail/";
    public static int REQUESTTAG_GET_TRANSACTION_DETAIL = 92;

    public static final String UPDATE_PAYMENT_REQUEST_FOR_SODEXO = BASE_URL + "updatePaymentRequestForSodexo/";
    public static int REQUESTTAG_UPDATE_PAYMENT_REQUEST_FOR_SODEXO = 93;

}
