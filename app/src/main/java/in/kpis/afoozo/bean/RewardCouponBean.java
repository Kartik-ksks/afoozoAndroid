package in.kpis.afoozo.bean;

import java.io.Serializable;

public class RewardCouponBean implements Serializable, Comparable<RewardCouponBean> {

    private CouponListBean couponListBean;
    private RewardsBean rewardsBean;
    private Long createdAt;

    public RewardCouponBean(CouponListBean couponListBean, Long createdAt) {
        this.couponListBean = couponListBean;
        this.createdAt = createdAt;
    }

    public RewardCouponBean(RewardsBean rewardsBean, Long createdAt) {
        this.rewardsBean = rewardsBean;
        this.createdAt = createdAt;
    }

    public CouponListBean getCouponListBean() {
        return couponListBean;
    }

    public void setCouponListBean(CouponListBean couponListBean) {
        this.couponListBean = couponListBean;
    }

    public RewardsBean getRewardsBean() {
        return rewardsBean;
    }

    public void setRewardsBean(RewardsBean rewardsBean) {
        this.rewardsBean = rewardsBean;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(RewardCouponBean o) {
        return getCreatedAt().compareTo(o.getCreatedAt());
    }
}
