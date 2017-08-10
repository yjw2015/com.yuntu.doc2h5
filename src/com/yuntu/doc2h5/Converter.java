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
 * ���þ���ͼ�ĵ�ת�����������ࡣ
 * <p>
 * ����ͼ��һ���ĵ�ת�����Ƽ�����񣬰����û��Ѹ����ĵ�ת�� HTML5(SVG)
 * ��ʽ�����ƶ���ȱ���ֿ������£�Ҳ�ܱ�������Ͱ�ʽ��ȫ���䣬֧�ָ���ƽ�����š�ת�����ɵ� HTML �����κνű������������غ�ɶ���ʹ�ã����֧��
 * 500M �ĵ���
 * </p>
 * 
 * @version 1.0
 */
public class Converter {

	/** �����԰������г��� */
	public final static String BUY_FROM_ALIYUN = "aliyun";

	/** �����Ծ���ͼ΢�Ź��ںš� */
	public final static String BUY_FROM_WEIXIN = "weixin";

	/** ����ͼ��Ȩ�롣���ȱ����Ȩ�룬ת���Ľ������ ������ͼ DEMO�� ˮӡ�� */
	private final String appCode;

	/** ���������������ָ����ϵͳĬ��Ϊ�����Ծ���ͼ΢�Ź��ںš� */
	private final String buyFrom;

	/** ����ͼ������������ */
	private String host = "https://server.9yuntu.cn";

	/**
	 * ��������ͼ�ĵ�ת���ࡣ�÷��������ṩ��Ȩ��͹��������������÷�����ת���Ľ������� ������ͼ DEMO�� ˮӡ��
	 */
	public Converter() {
		this(null, null);
	}

	/**
	 * ��������ͼ�ĵ�ת���࣬���ṩ��Ȩ��͹���������
	 * 
	 * @param appCode
	 *            ��Ȩ�롣�������Ȩ����Ч��ת���Ľ���ᱻ��� ������ͼ DEMO�� ˮӡ��
	 * @param buyFrom
	 *            ���������� ��ʹ�ó��� <code>Converter.BUY_FROM_ALIYUN</code> ��ʾ�����԰������г���
	 *            <code>Converter.BUY_FROM_WEIXIN</code> ��ʾ�����Ծ���ͼ΢�Ź��ںš�
	 */
	public Converter(String appCode, String buyFrom) {
		this.appCode = appCode;
		this.buyFrom = buyFrom;
		if (BUY_FROM_ALIYUN.equalsIgnoreCase(buyFrom)) {
			this.host = "https://api.9yuntu.cn";
		}
	}

	/**
	 * ת��ָ�����ĵ���
	 * 
	 * @param docUrl
	 *            ��ת�����ĵ� URL��
	 * @return ����һ�� <code>YuntuDoc</code> ʵ��, ���а������ĵ�ת��״̬����Ϣ��
	 * @throws YuntuException
	 *             �ĵ�ת���쳣��
	 */
	public YuntuDoc convert(String docUrl) throws YuntuException {
		System.out.print("����ͼת����ʼ...");
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
				System.out.println("ת���ɹ���");
			} else if (code == 1) {
				String docID = json.getString("docID");

				if (docID != null) {
					doc = this.queryStatus(docID, true);
				} else {
					doc.setCode(2);
					doc.setMessage("ת��ʧ�ܣ�");
					System.out.println("ת��ʧ�ܡ�");
				}
			} else if (code == 2) {
				String message = json.getString("retMsg");

				doc.setMessage(message);
				System.out.println("ת��ʧ��(" + message + ")��");
			}
		} catch (JSONException e) {
			doc.setCode(2);
			doc.setMessage("ת��ʧ�ܣ�");
			System.out.println("ת��ʧ��(" + e.getMessage() + ")��");
		} catch (IOException e) {
			throw new YuntuException(e);
		}
		return doc;
	}

	/**
	 * ��ѯת��״̬��
	 * 
	 * @param docID
	 *            �ĵ� ID��
	 * @return ����һ�� <code>YuntuDoc</code> ʵ��, ���а������ĵ�ת��״̬����Ϣ��
	 */
	public YuntuDoc queryStatus(String docID) {
		return queryStatus(docID, false);
	}

	/**
	 * ��ѯ�ĵ�ת��״̬��
	 * 
	 * @param docID
	 *            �ĵ� ID��
	 * @param autoLoop
	 *            <code>true</code> ����ĵ��������ڴ���״̬�����Զ���ѯ��<code>false</code> ���Զ���ѯ��
	 * @return ����һ�� <code>YuntuDoc</code> ʵ��, ���а������ĵ�ת��״̬����Ϣ��
	 */
	public YuntuDoc queryStatus(String docID, boolean autoLoop) {
		int retryTimes = 0;

		if (autoLoop) {
			retryTimes = 3000;
		}
		return doQueryStatus(docID, retryTimes);
	}

	/**
	 * ��ѯ�ĵ�ת��״̬��
	 * 
	 * @param docID
	 *            �ĵ� ID��
	 * @param retryTimes
	 *            �Զ���ѯ�Ĵ�����
	 * @return ����һ�� <code>YuntuDoc</code> ʵ��, ���а������ĵ�ת��״̬����Ϣ��
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
	 * ��ȡһ�������ĵ�������ҳ�����ݵ� HTML���������κ������ͽű��������غ��������ͼ����ʹ�á�
	 * 
	 * @param doc
	 *            ָ���� <code>YuntuDoc</code> ʵ����
	 * @return ����һ�� URL��ָ�������� HTML�����а����ĵ�������ҳ�����ݡ�
	 * @throws YuntuException
	 *             �ĵ�ת���쳣��
	 */
	public String getWholeHTML(YuntuDoc doc) throws YuntuException {
		List<String> outputResult = this.getOutputResult(doc, "html");

		if (outputResult != null && !outputResult.isEmpty()) {
			return outputResult.get(0);
		}
		return null;
	}

	/**
	 * ��ȡ�ĵ�����ҳ��� HTMLs��ÿҳ����һ�������� HTML���������κ������ͽű��������غ��������ͼ����ʹ�á�
	 * 
	 * @param doc
	 *            ָ���� <code>YuntuDoc</code> ʵ����
	 * @return ���ذ���һ�� URL �� <code>List</code> ʵ��������ÿ�� URL ָ��һ���ĵ�ҳ��� HTML��
	 * @throws YuntuException
	 *             �ĵ�ת������
	 */
	public List<String> getAllHTMLs(YuntuDoc doc) throws YuntuException {
		return this.getOutputResult(doc, "htmls");
	}

	/**
	 * ����ָ�������ͣ���ȡ�ĵ�ת����������в������κ������ͽű��������غ��������ͼ����ʹ�á�
	 * 
	 * @param doc
	 *            ָ���� <code>YuntuDoc</code> ʵ����
	 * @param outputType
	 *            ������͡������ <code>html</code>�� ����һ������Ϊ 1 �� <code>String</code>
	 *            �б���Ա��һ�� URL��ָ�������� HTML�����а����ĵ�������ҳ�����ݣ������ <code>htmls</code>
	 *            �����س���Ϊ�ĵ���ҳ���� <code>String</code> �б�ÿ����Ա��һ�� URL��ָ��ÿ���ĵ�ҳ���
	 *            HTML��
	 * @return ���ذ���һ�� URL �� <code>List</code> ʵ��������ÿ�� URL ָ��һ���ĵ�ҳ��� HTML��
	 * @throws YuntuException
	 *             �ĵ�ת���쳣��
	 */
	public List<String> getOutputResult(YuntuDoc doc, String outputType)
			throws YuntuException {

		if (doc == null || doc.getID() == null || !doc.isSuccess()) {
			throw new YuntuException("doc ״̬����");
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
	 * �����������ʾָ�����ĵ���
	 * 
	 * @param doc
	 *            ָ���� <code>YuntuDoc</code> ʵ����
	 * @throws YuntuException
	 *             �ĵ���ʾ�쳣��
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
