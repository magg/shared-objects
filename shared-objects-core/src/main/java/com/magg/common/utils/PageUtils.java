package com.magg.common.utils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Utilities to create Nextiva paging spec responses.
 */
@Component
@SuppressWarnings("unused")
public class PageUtils {

    public static final String FIRST = "first";
    public static final String NEXT = "next";
    public static final String PREV = "prev";
    public static final String LAST = "last";

    /**
     * Get all the possible links for a page result.
     * @param page the {@link Page} result
     * @param pageSize the page size
     * @param pageNumber the page number
     * @param sortField the field sorted on
     * @param sortDirection the direction sorted
     * @return array of header values in Nextiva format
     */
    public static Map<String, String> getPageLinks(Page<?> page,
                                    int pageSize, int pageNumber,
                                    String sortField, String sortDirection) {
        Map<String, String> links = new HashMap<>();
        long total = page.getTotalElements();
        int totalPages = page.getTotalPages();

        if (pageNumber == totalPages) {
            // Nowhere else to go, no links
            return links;
        }

        if (pageNumber > 1) {
            links.put(PREV,
                    getPaginationUrl(pageSize, pageNumber - 1, sortField, sortDirection, PREV)
            );
        }
        if (pageNumber < totalPages) {
            links.put(NEXT,
                    getPaginationUrl(pageSize, pageNumber + 1, sortField, sortDirection, NEXT)
            );
        }
        if (pageNumber + 1 < totalPages) {
            links.put(LAST,
                    getPaginationUrl(pageSize, totalPages, sortField, sortDirection, LAST)
            );
        }
        if (pageNumber - 1 > 1) {
            links.put(FIRST,
                    getPaginationUrl(pageSize, 1, sortField, sortDirection, FIRST)
            );
        }
        return links;
    }

    /**
     * Build a URL for a pagination link.
     * @param pageSize the size of the page
     * @param pageNumber the number of the page
     * @param sortField the field we are sorting on
     * @param sortDirection the direction we are sorting
     * @param relValue the 'rel' value
     * @return link header in Nextiva format
     */
    public static String getPaginationUrl(Integer pageSize, Integer pageNumber,
                                   String sortField, String sortDirection, String relValue) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder
                .fromCurrentRequestUri();
        if (pageSize != null) {
            builder = (ServletUriComponentsBuilder)builder.queryParam("pageSize", pageSize);
        }
        if (pageNumber != null) {
            builder = (ServletUriComponentsBuilder)builder.queryParam("pageNumber", pageNumber);
        }
        if (sortField != null) {
            builder = (ServletUriComponentsBuilder)builder.queryParam("sortField", sortField);
        }
        if (sortDirection != null) {
            builder = (ServletUriComponentsBuilder)builder.queryParam("sortDirection", sortDirection);
        }

        return String.format("%s", builder.build());
    }
}
