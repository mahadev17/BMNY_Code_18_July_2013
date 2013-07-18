package com.tbldevelopment.bmny.listselection;


public class EntryItem implements Item{

	public final String title,recieverId;
	public final String subtitle,email,mobile,friendid,date,status,userId,senderId,currentStatus;
	public final boolean isBlock;
	public final boolean isBestFrnd;
	public final int index;
	

	public EntryItem(int index,String userId,String senderId,String recieverId,String title, String subtitle,String currentStatus,boolean block, boolean bestfrnd,String emailid, String mobileNo, String frndId, String date,String status) {
		this.title = title;
		this.index = index;
		this.senderId=senderId;
		this.recieverId=recieverId;
		this.subtitle = subtitle;
		this.isBlock = block;
		this.isBestFrnd  = bestfrnd;
		this.email = emailid;
		this.mobile = mobileNo;
		this.friendid = frndId;
		this.date = date;
		this.userId=userId;
		this.currentStatus=currentStatus;
		this.status=status;
	}
	
	@Override
	public boolean isSection() {
		return false;
	}

}
