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
 * <p>
 * ��ʾ���ʾ���ͼ�ĵ�ת������ʱ�������쳣����������������ʱ���ֵĴ����Լ��ͻ����޷������ؽ����
 * ���磬�ڷ�������ʱ�������Ӳ����õȡ��쳣�а����˴�����Ϣ �������õ����߽����ض��Ĵ���
 * </p>
 */
public class YuntuException extends Exception {

	/** ϵͳ�Զ����ɵ�ϵ�кš� */
	private static final long serialVersionUID = -1173377338525899431L;

	/**
	 * �ø������쳣��Ϣ������ʵ����
	 * 
	 * @param message
	 *            �쳣��Ϣ��
	 */
	public YuntuException(String message) {
		super(message);
	}

	/**
	 * �ñ�ʾ�쳣ԭ��Ķ�������ʵ����
	 * 
	 * @param cause
	 *            �쳣ԭ��
	 */
	public YuntuException(Throwable cause) {
		super(cause);
	}
}
