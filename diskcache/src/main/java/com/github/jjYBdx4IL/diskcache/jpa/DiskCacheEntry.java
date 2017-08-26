/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.diskcache.jpa;

import com.github.jjYBdx4IL.diskcache.DiskCache;

import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Version;

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
@Entity
public class DiskCacheEntry {

    @Id
    @GeneratedValue
    private long id;

    @Basic
    @Column(length = DiskCache.MAX_KEY_LENGTH, nullable = true, unique = false)
    private String url;

    // null iff data is stored in separate file, identified by id
    @Lob
    @Column(nullable = true)
    private byte[] data;

    @Basic
    @Column(nullable = false)
    private long size;

    @Basic
    @Column(nullable = false)
    private long createdAt;

    @Version
    private long version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("DiskCacheEntry [id=");
		builder.append(id);
		builder.append(", url=");
		builder.append(url);
		builder.append(", data=");
		builder.append(data != null ? Arrays.toString(Arrays.copyOf(data, Math.min(data.length, maxLen))) : null);
		builder.append(", size=");
		builder.append(size);
		builder.append(", createdAt=");
		builder.append(createdAt);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
