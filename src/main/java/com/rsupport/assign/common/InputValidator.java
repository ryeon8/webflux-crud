package com.rsupport.assign.common;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.Errors;

/**
 * 입력값 검증 공통 interface.
 *
 * @author r3n
 * @param <T> 검증 대상 java bean
 */
public interface InputValidator<T> {

	/**
	 * 입력값 검증.
	 * 
	 * @param input  검증 대상 입력값
	 * @param errors 입력값 검증 결과
	 * @param params 검증에 이용할 추가 인자
	 */
	default void validate(T input, Errors errors, Object... params) {
		if (!errors.hasErrors()) {
			List<Pair<String, String>> invalidList = validate(input, params);
			invalidList.removeIf(Objects::isNull);
			for (Pair<String, String> invalid : invalidList) {
				if (StringUtils.isBlank(invalid.getLeft())) {
					errors.reject(null, invalid.getRight());
				} else {
					errors.rejectValue(invalid.getLeft(), null, invalid.getRight());
				}
			}
		}
	}

	/**
	 * 입력값 검증.
	 * 
	 * @param input  검증 대상 입력값
	 * @param params 검증에 이용할 추가 인자
	 * @return left: 검증 실패 필드명, right: 검증 실패 사유
	 */
	List<Pair<String, String>> validate(T input, Object... params);

}
