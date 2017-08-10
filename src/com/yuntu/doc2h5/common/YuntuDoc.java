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
package com.yuntu.doc2h5.common;

/**
 * �ĵ�״̬��Ϣ��
 */
public class YuntuDoc {

	/** �ĵ� ID */
	private String id = null;

	/** �ĵ�״̬�� (0--ת���ɹ���1--ת�������У�2--ת��ʧ��)��Ĭ��ֵ�� 1�� */
	private int code = 1;

	/** �ĵ�״̬��Ϣ�� */
	private String message = "";

	/** �ĵ�״̬ҳ�� URL�� */
	private String statusPageURL = null;

	/**
	 * ���캯����
	 */
	public YuntuDoc() {
	}

	/**
	 * ���캯����
	 * 
	 * @param docID
	 *            �ĵ� ID��
	 */
	public YuntuDoc(String docID) {
		this.setID(docID);
	}

	/**
	 * �����ĵ� ID��
	 * 
	 * @return �ĵ� ID��
	 */
	public String getID() {
		return id;
	}

	/**
	 * �����ĵ� ID��
	 * 
	 * @param docID
	 *            �ĵ� ID��
	 */
	public void setID(String docID) {
		this.id = docID;
	}

	/**
	 * �ж��ĵ�ת���Ƿ�ɹ���
	 * 
	 * @return <code>true</code> ת���ɹ���<code>false</code> ת��ʧ�ܡ�
	 */
	public boolean isSuccess() {
		return this.getID() != null && this.getCode() == 0;
	}

	/**
	 * �����ĵ�״̬�롣
	 * 
	 * @return �ĵ�״̬�롣0--ת���ɹ���1--ת�������У�2--ת��ʧ�ܡ�
	 */
	public int getCode() {
		return code;
	}

	/**
	 * �����ĵ�״̬�롣
	 * 
	 * @param code
	 *            �ĵ�״̬�롣0--ת���ɹ���1--ת�������У�2--ת��ʧ�ܡ�
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * �����ĵ�״̬��Ϣ��
	 * 
	 * @return �ĵ�״̬��Ϣ��
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * �����ĵ�״̬��Ϣ��
	 * 
	 * @param message
	 *            �ĵ�״̬��Ϣ��
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * �����ĵ�״̬ҳ�� URL��
	 * 
	 * @return �ĵ�״̬ҳ�� URL��
	 */
	public String getStatusPage() {
		return statusPageURL;
	}

	/**
	 * �����ĵ�״̬ҳ�档
	 * 
	 * @param statusPageURL
	 *            �ĵ�״̬ҳ�� URL��
	 */
	public void setStatusPage(String statusPageURL) {
		this.statusPageURL = statusPageURL;
	}
}
