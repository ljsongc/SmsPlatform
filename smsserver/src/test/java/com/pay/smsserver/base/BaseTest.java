package com.pay.smsserver.base;


import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class BaseTest {

	@Test
	public void jsonTest(){
		JSONObject jsonObject = new JSONObject("{\"ret\":0,\"result\":[{\"msgid\":\"17120111331734234\",\"reportTime\":\"1712011133\",\"mobile\":\"18601344991\",\"status\":\"DISTURB\",\"statusDesc\":null},{\"msgid\":\"17120111331734234\",\"reportTime\":\"1712011133\",\"mobile\":\"17601027017\",\"status\":\"DISTURB\",\"statusDesc\":null}]}");
		Integer ret = jsonObject.has("ret") ? jsonObject.getInt("ret") : null;
		JSONArray jsonArray = jsonObject.has("result") ? jsonObject.getJSONArray("result") : null;
		if(ret == 0 && jsonArray != null){
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject object = jsonArray.getJSONObject(i);
				String description = object.isNull("statusDesc") ? null : object.getString("statusDesc");
				Assert.assertEquals(null, description);
			}
		}
	}
}
