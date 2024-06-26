/*
 * Copyright 2013-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.kubernetes.client.config;

import java.util.LinkedHashSet;
import java.util.function.Supplier;

import org.springframework.cloud.kubernetes.commons.config.ConfigUtils;
import org.springframework.cloud.kubernetes.commons.config.MultipleSourcesContainer;
import org.springframework.cloud.kubernetes.commons.config.NamedConfigMapNormalizedSource;
import org.springframework.cloud.kubernetes.commons.config.NamedSourceData;

/**
 * Provides an implementation of {@link KubernetesClientContextToSourceData} for a named
 * config map.
 *
 * @author wind57
 */
final class NamedConfigMapContextToSourceDataProvider implements Supplier<KubernetesClientContextToSourceData> {

	NamedConfigMapContextToSourceDataProvider() {
	}

	@Override
	public KubernetesClientContextToSourceData get() {

		return context -> {

			NamedConfigMapNormalizedSource source = (NamedConfigMapNormalizedSource) context.normalizedSource();

			return new NamedSourceData() {
				@Override
				protected String generateSourceName(String target, String sourceName, String namespace,
						String[] activeProfiles) {
					if (source.appendProfileToName()) {
						return ConfigUtils.sourceName(target, sourceName, namespace, activeProfiles);
					}
					return super.generateSourceName(target, sourceName, namespace, activeProfiles);
				}

				@Override
				public MultipleSourcesContainer dataSupplier(LinkedHashSet<String> sourceNames) {
					return KubernetesClientConfigUtils.configMapsDataByName(context.client(), context.namespace(),
							sourceNames, context.environment(), context.includeDefaultProfileData());
				}
			}.compute(source.name().orElseThrow(), source.prefix(), source.target(), source.profileSpecificSources(),
					source.failFast(), context.namespace(), context.environment().getActiveProfiles());
		};

	}

}
