/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.wso2.carbon.apimgt.rest.api.publisher.utils.mappings;

import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.TierDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.TierListDTO;
import org.wso2.carbon.apimgt.rest.api.util.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for mapping APIM core tier related objects into REST API Tier related DTOs
 */
public class TierMappingUtil {

    /**
     * Converts a List object of Tiers into a DTO
     *
     * @param tiers  a list of Tier objects
     * @param limit  max number of objects returned
     * @param offset starting index
     * @return TierListDTO object containing TierDTOs
     */
    public static TierListDTO fromTierListToDTO(List<Tier> tiers, int limit,
            int offset) {
        TierListDTO tierListDTO = new TierListDTO();
        List<TierDTO> tierDTOs = tierListDTO.getList();
        if (tierDTOs == null) {
            tierDTOs = new ArrayList<>();
            tierListDTO.setList(tierDTOs);
        }

        //identifying the proper start and end indexes
        int size = tiers.size();
        int start = offset < size && offset >= 0 ? offset : Integer.MAX_VALUE;
        int end = offset + limit - 1 <= size - 1 ? offset + limit - 1 : size - 1;

        for (int i = start; i <= end; i++) {
            Tier tier = tiers.get(i);
            tierDTOs.add(fromTiertoDTO(tier));
        }
        tierListDTO.setCount(tierDTOs.size());
        return tierListDTO;
    }

    /**
     * Sets pagination urls for a TierListDTO object given pagination parameters and url parameters
     *
     * @param tierListDTO a TierListDTO object
     * @param limit       max number of objects returned
     * @param offset      starting index
     * @param size        max offset
     */
    public static void setPaginationParams(TierListDTO tierListDTO, int limit, int offset, int size) {

        String paginatedPrevious = "";
        String paginatedNext = "";

        Map<String, Integer> paginatedParams = RestApiUtil.getPaginationParams(offset, limit, size);

        if (paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET) != null) {
            paginatedPrevious = RestApiUtil
                    .getTiersPaginatedURL(
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_PREVIOUS_LIMIT));
        }

        if (paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET) != null) {
            paginatedNext = RestApiUtil
                    .getTiersPaginatedURL(paginatedParams.get(RestApiConstants.PAGINATION_NEXT_OFFSET),
                            paginatedParams.get(RestApiConstants.PAGINATION_NEXT_LIMIT));
        }

        tierListDTO.setNext(paginatedNext);
        tierListDTO.setPrevious(paginatedPrevious);
    }

    /**
     * Converts a Tier object into TierDTO
     *
     * @param tier Tier object
     * @return TierDTO corresponds to Tier object
     */
    public static TierDTO fromTiertoDTO(Tier tier) {
        TierDTO dto = new TierDTO();
        dto.setName(tier.getName());
        dto.setDisplayName(tier.getDisplayName());
        dto.setDescription(tier.getDescription());
        dto.setRequestCount(tier.getRequestCount());
        dto.setUnitTime(tier.getUnitTime());
        dto.setStopOnQuotaReach(tier.isStopOnQuotaReached());
        if (tier.getTierPlan() != null) {
            dto.setTierPlan(TierDTO.TierPlanEnum.valueOf(tier.getTierPlan()));
        }
        if (tier.getTierAttributes() != null) {
            Map<String, String> additionalProperties = new HashMap<>();
            for (String key : tier.getTierAttributes().keySet()) {
                additionalProperties.put(key, tier.getTierAttributes().get(key).toString());
            }
            dto.setAttributes(additionalProperties);
        }
        return dto;
    }

    /**
     * Converts a TierDTO object into Tier in APIM core
     *
     * @param dto TierDTO object
     * @return Tier corresponds to TierDTO object
     */
    public static Tier fromDTOtoTier(TierDTO dto) {
        Tier tier = new Tier(dto.getName());
        tier.setDisplayName(dto.getDisplayName());
        tier.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        tier.setRequestCount(dto.getRequestCount());
        tier.setUnitTime(dto.getUnitTime());
        tier.setStopOnQuotaReached(dto.getStopOnQuotaReach());
        tier.setTierPlan(dto.getTierPlan().toString());
        if (dto.getAttributes() != null) {
            Map<String, Object> tierAttributes = new HashMap<>();
            for (String key : dto.getAttributes().keySet()) {
                Object value = dto.getAttributes().get(key);
                tierAttributes.put(key, value);
            }
            tier.setTierAttributes(tierAttributes);
        }
        return tier;
    }
}
