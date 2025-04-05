package in.kpis.afoozo.okhttpServcice.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ResponseClass<T> implements ParameterizedType {

    private String completePacket;
    private int errorCode;
    private String message;
    private boolean success;
    private T responsePacket;
    private ArrayList<String> listOfErrors;

//    @SerializedName("responsePacket")
//    private ArrayList<T> responsePacketX;


    public <M> ArrayList<M> getArrayListFromResponse() {
        Type objType = new TypeToken<ResponseClass<ArrayList<M>>>() {
        }.getType();
        ResponseClass<ArrayList<M>> responseClass1 = new Gson().fromJson(getCompletePacket(), objType);
        return responseClass1.getResponsePacket();
    }

    public String getCompletePacket() {
        return completePacket;
    }

    public void setCompletePacket(String completePacket) {
        this.completePacket = completePacket;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResponsePacket() {
        return responsePacket;
    }

    public ArrayList<String> getListOfErrors() {
        return listOfErrors;
    }

    public void setListOfErrors(ArrayList<String> listOfErrors) {
        this.listOfErrors = listOfErrors;
    }

    public void setResponsePacket(T responsePacket) {
        this.responsePacket = responsePacket;
    }

//    public ArrayList<T> getResponsePacketX() {
//        return responsePacketX;
//    }
//
//    public void setResponsePacketX(ArrayList<T> responsePacketX) {
//        this.responsePacketX = responsePacketX;
//    }

    public static class ResponsePacketBean {
        /**
         * recordId : 78e17240-4ab0-4004-b5df-263b08f33635
         * active : true
         * name : Jodhpur
         * cost : 4000
         */

        private String recordId;
        private boolean active;
        private String name;
        private int cost;

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }
    }


    public class ResponseDefault {
        private float androidVersionCode;

        public float getAndroidVersionCode() {
            return androidVersionCode;
        }

        public void setAndroidVersionCode(float androidVersionCode) {
            this.androidVersionCode = androidVersionCode;
        }
    }

    public ResponseClass(Class<?> wrapped) {
        this.wrapped = wrapped;
    }

    private Class<?> wrapped;

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrapped};
    }

    @Override
    public Type getRawType() {
        return ResponseClass.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }


}
