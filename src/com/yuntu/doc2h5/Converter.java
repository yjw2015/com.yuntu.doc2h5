/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.yuntu.doc2h5;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yuntu.doc2h5.common.YuntuDoc;
import com.yuntu.doc2h5.common.YuntuException;
import com.yuntu.doc2h5.internal.HttpUtils;

/**
 * 调用九云图文档转换服务的入口类。
 * <p>
 * 九云图是一个文档转换的云计算服务，帮助用户把各类文档转成 HTML5(SVG)
 * 格式。在移动端缺少字库的情况下，也能保持字体和版式完全不变，支持高清平滑缩放。转换生成的 HTML 不含任何脚本和外链，下载后可独立使用，最大支持
 * 500M 文档。
 * </p>
 * 
 * @version 1.0
 */
public class Converter {

	/** 购买自阿里云市场。 */
	public final static String BUY_FROM_ALIYUN = "aliyun";

	/** 购买自九云图微信公众号。 */
	public final static String BUY_FROM_WEIXIN = "weixin";

	/** 九云图授权码。如果缺少授权码，转换的结果带有 “九云图 DEMO” 水印。 */
	private final String appCode;

	/** 购买渠道。如果不指定，系统默认为购买自九云图微信公众号。 */
	private final String buyFrom;

	/** 九云图服务器域名。 */
	private String host = "https://server.9yuntu.cn";

	/**
	 * 构建九云图文档转换类。该方法无需提供授权码和购买渠道，属试用方法，转换的结果会带有 “九云图 DEMO” 水印。
	 */
	public Converter() {
		this(null, null);
	}

	/**
	 * 构建九云图文档转换类，需提供授权码和购买渠道。
	 * 
	 * @param appCode
	 *            授权码。如果该授权码无效，转换的结果会被添加 “九云图 DEMO” 水印。
	 * @param buyFrom
	 *            购买渠道。 可使用常量 <code>Converter.BUY_FROM_ALIYUN</code> 表示购买自阿里云市场，
	 *            <code>Converter.BUY_FROM_WEIXIN</code> 表示购买自九云图微信公众号。
	 */
	public Converter(String appCode, String buyFrom) {
		this.appCode = appCode;
		this.buyFrom = buyFrom;
		if (BUY_FROM_ALIYUN.equalsIgnoreCase(buyFrom)) {
			this.host = "https://api.9yuntu.cn";
		}
	}

	/**
	 * 转换指定的文档。
	 * 
	 * @param docUrl
	 *            被转换的文档 URL。
	 * @return 返回一个 <code>YuntuDoc</code> 实例, 其中包含了文档转换状态等信息。
	 * @throws YuntuException
	 *             文档转换异常。
	 */
	public YuntuDoc convert(String docUrl) throws YuntuException {
		System.out.print("九云图转换开始...");
		String path = "/execute/Convert";
		Map<String, String> querys = new HashMap<String, String>();
		Map<String, String> headers = new HashMap<String, String>();

		headers.put("Content-Type", "application/json");
		querys.put("docURL", docUrl);
		if (this.appCode != null) {
			if (BUY_FROM_ALIYUN.equalsIgnoreCase(this.buyFrom)) {
				headers.put("Authorization", "APPCODE " + this.appCode);
			} else {
				querys.put("yuntuKey", this.appCode);
			}
		}

		YuntuDoc doc = new YuntuDoc();

		try {
			HttpResponse response = HttpUtils.doGet(host, path, "GET", headers,
					querys);

			String body = EntityUtils.toString(response.getEntity());

			JSONObject json = new JSONObject(body);
			int code = json.getInt("retCode");
			String statusPage = json.getString("docStatusPage");

			doc.setCode(code);
			doc.setStatusPage(statusPage);
			if (code == 0) {
				String docID = json.getString("docID");

				doc.setID(docID);
				System.out.println("转换成功。");
			} else if (code == 1) {
				String docID = json.getString("docID");

				if (docID != null) {
					doc = this.queryStatus(docID, true);
				} else {
					doc.setCode(2);
					doc.setMessage("转换失败！");
					System.out.println("转换失败。");
				}
			} else if (code == 2) {
				String message = json.getString("retMsg");

				doc.setMessage(message);
				System.out.println("转换失败(" + message + ")。");
			}
		} catch (JSONException e) {
			doc.setCode(2);
			doc.setMessage("转换失败！");
			System.out.println("转换失败(" + e.getMessage() + ")。");
		} catch (IOException e) {
			throw new YuntuException(e);
		}
		return doc;
	}

	/**
	 * 查询转换状态。
	 * 
	 * @param docID
	 *            文档 ID。
	 * @return 返回一个 <code>YuntuDoc</code> 实例, 其中包含了文档转换状态等信息。
	 */
	public YuntuDoc queryStatus(String docID) {
		return queryStatus(docID, false);
	}

	/**
	 * 查询文档转换状态。
	 * 
	 * @param docID
	 *            文档 ID。
	 * @param autoLoop
	 *            <code>true</code> 如果文档处于正在处理状态，则自动轮询；<code>false</code> 不自动轮询。
	 * @return 返回一个 <code>YuntuDoc</code> 实例, 其中包含了文档转换状态等信息。
	 */
	public YuntuDoc queryStatus(String docID, boolean autoLoop) {
		int retryTimes = 0;

		if (autoLoop) {
			retryTimes = 3000;
		}
		return doQueryStatus(docID, retryTimes);
	}

	/**
	 * 查询文档转换状态。
	 * 
	 * @param docID
	 *            文档 ID。
	 * @param retryTimes
	 *            自动轮询的次数。
	 * @return 返回一个 <code>YuntuDoc</code> 实例, 其中包含了文档转换状态等信息。
	 */
	private YuntuDoc doQueryStatus(String docID, int retryTimes) {
		System.out.print(".");

		YuntuDoc doc = new YuntuDoc(docID);
		String path = "/execute/QueryStatus";
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> querys = new HashMap<String, String>();

		headers.put("Content-Type", "application/json");
		querys.put("docID", docID);

		try {
			HttpResponse response = HttpUtils.doGet(host, path, "GET", headers,
					querys);

			if (response != null) {
				String body = EntityUtils.toString(response.getEntity());

				if (body != null) {
					JSONObject json = new JSONObject(body);
					int code = json.getInt("retCode");
					String statusPage = json.getString("docStatusPage");

					doc.setCode(code);
					doc.setStatusPage(statusPage);
					if (code == 1 || code == 2) {
						String message = json.getString("retMsg");

						doc.setMessage(message);
					}
				}
			}
		} catch (Throwable e) {
			doc.setCode(1);
		}
		if (doc.getCode() == 1) {
			if (retryTimes > 0) {
				return doQueryStatus(docID, --retryTimes);
			}
		}
		return doc;
	}

	/**
	 * 获取一个包含文档的所有页面内容的 HTML，其中无任何外链和脚本，可下载后脱离九云图独立使用。
	 * 
	 * @param doc
	 *            指定的 <code>YuntuDoc</code> 实例。
	 * @return 返回一个 URL，指向完整的 HTML，其中包含文档的所有页面内容。
	 * @throws YuntuException
	 *             文档转换异常。
	 */
	public String getWholeHTML(YuntuDoc doc) throws YuntuException {
		List<String> outputResult = this.getOutputResult(doc, "html");

		if (outputResult != null && !outputResult.isEmpty()) {
			return outputResult.get(0);
		}
		return null;
	}

	/**
	 * 获取文档所有页面的 HTMLs，每页生成一个独立的 HTML，其中无任何外链和脚本，可下载后脱离九云图独立使用。
	 * 
	 * @param doc
	 *            指定的 <code>YuntuDoc</code> 实例。
	 * @return 返回包含一组 URL 的 <code>List</code> 实例，其中每个 URL 指向一个文档页面的 HTML。
	 * @throws YuntuException
	 *             文档转换出错。
	 */
	public List<String> getAllHTMLs(YuntuDoc doc) throws YuntuException {
		return this.getOutputResult(doc, "htmls");
	}

	/**
	 * 根据指定的类型，获取文档转换结果，其中不包含任何外链和脚本，可下载后脱离九云图独立使用。
	 * 
	 * @param doc
	 *            指定的 <code>YuntuDoc</code> 实例。
	 * @param outputType
	 *            输出类型。如果是 <code>html</code>， 返回一个长度为 1 的 <code>String</code>
	 *            列表，成员是一个 URL，指向完整的 HTML，其中包含文档的所有页面内容；如果是 <code>htmls</code>
	 *            ，返回长度为文档总页数的 <code>String</code> 列表，每个成员是一个 URL，指向每个文档页面的
	 *            HTML。
	 * @return 返回包含一组 URL 的 <code>List</code> 实例，其中每个 URL 指向一个文档页面的 HTML。
	 * @throws YuntuException
	 *             文档转换异常。
	 */
	public List<String> getOutputResult(YuntuDoc doc, String outputType)
			throws YuntuException {

		if (doc == null || doc.getID() == null || !doc.isSuccess()) {
			throw new YuntuException("doc 状态错误！");
		}

		String path = "/execute/GetOutputResult";
		List<String> outputURLs = new ArrayList<String>();
		Map<String, String> headers = new HashMap<String, String>();
		Map<String, String> querys = new HashMap<String, String>();

		headers.put("Content-Type", "application/json");
		querys.put("docID", doc.getID());
		querys.put("outputType", outputType);

		try {
			HttpResponse response = HttpUtils.doGet(host, path, "GET", headers,
					querys);

			if (response != null) {
				String body = EntityUtils.toString(response.getEntity());

				if (body != null) {
					JSONObject json = new JSONObject(body);
					JSONArray urlArray = json.getJSONArray("outputURLs");

					for (int i = 0; i < urlArray.length(); i++) {
						outputURLs.add(urlArray.getString(i));
					}
				}
			}
		} catch (JSONException e) {
			throw new YuntuException(e);
		} catch (IOException e) {
			throw new YuntuException(e);
		}
		return outputURLs;
	}

	/**
	 * 打开浏览器，显示指定的文档。
	 * 
	 * @param doc
	 *            指定的 <code>YuntuDoc</code> 实例。
	 * @throws YuntuException
	 *             文档显示异常。
	 */
	public void showDoc(YuntuDoc doc) throws YuntuException {
		if (doc != null && doc.isSuccess()) {
			try {
				String path = "/execute/View";
				URI uri = new URI(host + path + "?docID=" + doc.getID());

				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				throw new YuntuException(e);
			} catch (URISyntaxException e) {
				throw new YuntuException(e);
			}
		}
	}
}
