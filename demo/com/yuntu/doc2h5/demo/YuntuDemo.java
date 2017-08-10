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
package com.yuntu.doc2h5.demo;

import com.yuntu.doc2h5.Converter;
import com.yuntu.doc2h5.common.YuntuDoc;
import com.yuntu.doc2h5.common.YuntuException;

/**
 * ����һ����ʾ����ͼ Java SDK �� Demo ���򣬿�ֱ�����С�
 * 
 * @version 1.0
 */
public class YuntuDemo {

	/** ������ʾ�������ĵ��� */
	private static String demoDoc = "https://image2.9yuntu.cn/resources/api/����ͼAPIʹ��˵��.docx";

	/**
	 * ����ͼ Demo ������
	 * 
	 * @param args
	 *            �����в�����
	 * @throws YuntuException
	 *             �ĵ�ת���쳣��
	 */
	public static void main(String[] args) throws YuntuException {
		Converter converter = new com.yuntu.doc2h5.Converter();
		YuntuDoc doc = converter.convert(demoDoc);

		if (doc.isSuccess()) {

			// �������������ҳ��ʽչ��ת������ĵ���
			converter.showDoc(doc);

			// ����һ�������ĵ�������ҳ�����ݵ� HTML���������κ������ͽű��������غ��������ͼ����ʹ�á�
			String url = converter.getWholeHTML(doc);

			System.out.println("������: " + url);
		} else {
			System.out.println("������Ϣ: " + doc.getMessage());
		}
	}
}
