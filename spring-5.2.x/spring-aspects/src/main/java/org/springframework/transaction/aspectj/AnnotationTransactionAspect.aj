/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.transaction.aspectj;

import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Concrete AspectJ transaction aspect using Spring's
 * {@link org.springframework.transaction.annotation.Transactional} annotation.
 *
 * <p>When using this aspect, you <i>must</i> annotate the implementation class
 * (and/or methods within that class), <i>not</i> the interface (if any) that
 * the class implements. AspectJ follows Java's rule that annotations on
 * interfaces are <i>not</i> inherited.
 *
 * <p>An @Transactional annotation on a class specifies the default transaction
 * semantics for the execution of any <b>public</b> operation in the class.
 *
 * <p>An @Transactional annotation on a method within the class overrides the
 * default transaction semantics given by the class annotation (if present).
 * Any method may be annotated (regardless of visibility). Annotating
 * non-public methods directly is the only way to get transaction demarcation
 * for the execution of such operations.
 *
 * @author Rod Johnson
 * @author Ramnivas Laddad
 * @author Adrian Colyer
 * @since 2.0
 * @see org.springframework.transaction.annotation.Transactional
 */
public aspect AnnotationTransactionAspect extends AbstractTransactionAspect {

	public AnnotationTransactionAspect() {
		super(new AnnotationTransactionAttributeSource(false));
	}

	/**
	 * Matches the execution of any public method in a type with the Transactional
	 * annotation, or any subtype of a type with the Transactional annotation.
	 */
	/**
	 *  作用在有Transactional注解或者包含Transactional的注解的public方法
	 */
	private pointcut executionOfAnyPublicMethodInAtTransactionalType() :
		execution(public * ((@Transactional *)+).*(..)) && within(@Transactional *);

	/**
	 * Matches the execution of any method with the Transactional annotation.
	 */
	/**
	 * 作用在任何有Transactional注解的方法
	 */
	private pointcut executionOfTransactionalMethod() :
		execution(@Transactional * *(..));

	/**
	 * Definition of pointcut from super aspect - matched join points
	 * will have Spring transaction management applied.
	 */
	/**
	 * 父类抽象方法，满足上面任一方法条件且满足父类条件的
	 */
	protected pointcut transactionalMethodExecution(Object txObject) :
		(executionOfAnyPublicMethodInAtTransactionalType() || executionOfTransactionalMethod() ) && this(txObject);

}
