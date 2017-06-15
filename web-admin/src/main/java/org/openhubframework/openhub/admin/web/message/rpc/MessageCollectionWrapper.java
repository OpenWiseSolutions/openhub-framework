package org.openhubframework.openhub.admin.web.message.rpc;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.openhubframework.openhub.admin.web.common.rpc.CollectionWrapper;
import org.springframework.core.convert.converter.Converter;


/**
 * Messages specific extension of collection wrapper.
 *
 * @author Karel Kovarik
 * @see CollectionWrapper
 */
public class MessageCollectionWrapper<T> extends CollectionWrapper<T> {

    private long limit;
    private long totalElements;

    public <S> MessageCollectionWrapper(Converter<? super S, ? extends T> converter, List<S> data, long limit, long totalElements) {
        super(converter, data);
        this.limit = limit;
        this.totalElements = totalElements;
    }

    public long getLimit() {
        return limit;
    }

    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .appendSuper(super.toString())
                .append("limit", limit)
                .append("totalElements", totalElements)
                .toString();
    }
}
