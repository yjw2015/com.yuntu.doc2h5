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
 * 这是一个演示九云图 Java SDK 的 Demo 程序，可直接运行。
 * 
 * @version 1.0
 */
public class YuntuDemo {

	/** 用来演示的样例文档。 */
	private static String demoDoc = "https://image2.9yuntu.cn/resources/api/九云图API使用说明.docx";

	/**
	 * 九云图 Demo 主程序。
	 * 
	 * @param args
	 *            命令行参数。
	 * @throws YuntuException
	 *             文档转换异常。
	 */
	public static void main(String[] args) throws YuntuException {
		Converter converter = new com.yuntu.doc2h5.Converter();
		YuntuDoc doc = converter.convert(demoDoc);

		if (doc.isSuccess()) {

			// 打开浏览器，以网页形式展现转换后的文档。
			converter.showDoc(doc);

			// 生成一个包含文档的所有页面内容的 HTML，其中无任何外链和脚本，可下载后脱离九云图独立使用。
			String url = converter.getWholeHTML(doc);

			System.out.println("输出结果: " + url);
		} else {
			System.out.println("错误信息: " + doc.getMessage());
		}
	}
}
