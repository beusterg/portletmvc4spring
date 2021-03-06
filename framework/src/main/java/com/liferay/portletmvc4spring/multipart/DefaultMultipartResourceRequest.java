/**
 * Copyright (c) 2000-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.multipart;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ResourceRequest;
import javax.portlet.filter.ResourceRequestWrapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import org.springframework.web.multipart.MultipartFile;


/**
 * Default implementation of the {@link MultipartResourceRequest} interface. Provides management of pre-generated
 * parameter values.
 *
 * @author  Juergen Hoeller
 * @author  Arjen Poutsma
 * @author  Neil Griffin
 * @since   5.1
 * @see     PortletMultipartResolver
 */
public class DefaultMultipartResourceRequest extends ResourceRequestWrapper implements MultipartResourceRequest {

	private MultiValueMap<String, MultipartFile> multipartFiles;

	private Map<String, String[]> multipartParameters;

	private Map<String, String> multipartParameterContentTypes;

	/**
	 * Wrap the given Portlet ResourceRequest in a MultipartResourceRequest.
	 *
	 * @param  request   the request to wrap
	 * @param  mpFiles   a map of the multipart files
	 * @param  mpParams  a map of the parameters to expose, with Strings as keys and String arrays as values
	 */
	public DefaultMultipartResourceRequest(ResourceRequest request, MultiValueMap<String, MultipartFile> mpFiles,
		Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {

		super(request);
		setMultipartFiles(mpFiles);
		setMultipartParameters(mpParams);
		setMultipartParameterContentTypes(mpParamContentTypes);
	}

	/**
	 * Wrap the given Portlet ResourceRequest in a MultipartResourceRequest.
	 *
	 * @param  request  the request to wrap
	 */
	protected DefaultMultipartResourceRequest(ResourceRequest request) {
		super(request);
	}

	@Override
	public MultipartFile getFile(String name) {
		return getMultipartFiles().getFirst(name);
	}

	@Override
	public Map<String, MultipartFile> getFileMap() {
		return getMultipartFiles().toSingleValueMap();
	}

	@Override
	public Iterator<String> getFileNames() {
		return getMultipartFiles().keySet().iterator();
	}

	@Override
	public List<MultipartFile> getFiles(String name) {
		List<MultipartFile> multipartFiles = getMultipartFiles().get(name);

		if (multipartFiles != null) {
			return multipartFiles;
		}
		else {
			return Collections.emptyList();
		}
	}

	@Override
	public MultiValueMap<String, MultipartFile> getMultiFileMap() {
		return getMultipartFiles();
	}

	@Override
	public String getMultipartContentType(String paramOrFileName) {
		MultipartFile file = getFile(paramOrFileName);

		if (file != null) {
			return file.getContentType();
		}
		else {
			return getMultipartParameterContentTypes().get(paramOrFileName);
		}
	}

	@Override
	public String getParameter(String name) {
		String[] values = getMultipartParameters().get(name);

		if (values != null) {
			return ((values.length > 0) ? values[0] : null);
		}

		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> paramMap = new HashMap<String, String[]>();
		paramMap.putAll(super.getParameterMap());
		paramMap.putAll(getMultipartParameters());

		return paramMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Set<String> paramNames = new HashSet<String>();
		Enumeration<String> paramEnum = super.getParameterNames();

		while (paramEnum.hasMoreElements()) {
			paramNames.add(paramEnum.nextElement());
		}

		paramNames.addAll(getMultipartParameters().keySet());

		return Collections.enumeration(paramNames);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = getMultipartParameters().get(name);

		if (values != null) {
			return values;
		}

		return super.getParameterValues(name);
	}

	/**
	 * Obtain the MultipartFile Map for retrieval, lazily initializing it if necessary.
	 *
	 * @see  #initializeMultipart()
	 */
	protected MultiValueMap<String, MultipartFile> getMultipartFiles() {

		if (this.multipartFiles == null) {
			initializeMultipart();
		}

		return this.multipartFiles;
	}

	/**
	 * Obtain the multipart parameter content type Map for retrieval, lazily initializing it if necessary.
	 *
	 * @see  #initializeMultipart()
	 */
	protected Map<String, String> getMultipartParameterContentTypes() {

		if (this.multipartParameterContentTypes == null) {
			initializeMultipart();
		}

		return this.multipartParameterContentTypes;
	}

	/**
	 * Obtain the multipart parameter Map for retrieval, lazily initializing it if necessary.
	 *
	 * @see  #initializeMultipart()
	 */
	protected Map<String, String[]> getMultipartParameters() {

		if (this.multipartParameters == null) {
			initializeMultipart();
		}

		return this.multipartParameters;
	}

	/**
	 * Lazily initialize the multipart request, if possible. Only called if not already eagerly initialized.
	 */
	protected void initializeMultipart() {
		throw new IllegalStateException("Multipart request not initialized");
	}

	/**
	 * Set a Map with parameter names as keys and list of MultipartFile objects as values. To be invoked by subclasses
	 * on initialization.
	 */
	protected final void setMultipartFiles(MultiValueMap<String, MultipartFile> multipartFiles) {
		this.multipartFiles = new LinkedMultiValueMap<String, MultipartFile>(Collections.unmodifiableMap(
					multipartFiles));
	}

	/**
	 * Set a Map with parameter names as keys and content type Strings as values. To be invoked by subclasses on
	 * initialization.
	 */
	protected final void setMultipartParameterContentTypes(Map<String, String> multipartParameterContentTypes) {
		this.multipartParameterContentTypes = multipartParameterContentTypes;
	}

	/**
	 * Set a Map with parameter names as keys and String array objects as values. To be invoked by subclasses on
	 * initialization.
	 */
	protected final void setMultipartParameters(Map<String, String[]> multipartParameters) {
		this.multipartParameters = multipartParameters;
	}

}
