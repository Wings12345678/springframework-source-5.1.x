/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * Enables Spring's annotation-driven transaction management capability, similar to
 * the support found in Spring's {@code <tx:*>} XML namespace. To be used on
 * {@link org.springframework.context.annotation.Configuration @Configuration}
 * classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableTransactionManagement
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         // configure and return a class having &#064;Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // configure and return the necessary JDBC DataSource
 *     }
 *
 *     &#064;Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 * }</pre>
 *
 * <p>For reference, the example above can be compared to the following Spring XML
 * configuration:
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;tx:annotation-driven/&gt;
 *
 *     &lt;bean id="fooRepository" class="com.foo.JdbcFooRepository"&gt;
 *         &lt;constructor-arg ref="dataSource"/&gt;
 *     &lt;/bean&gt;
 *
 *     &lt;bean id="dataSource" class="com.vendor.VendorDataSource"/&gt;
 *
 *     &lt;bean id="transactionManager" class="org.sfwk...DataSourceTransactionManager"&gt;
 *         &lt;constructor-arg ref="dataSource"/&gt;
 *     &lt;/bean&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * In both of the scenarios above, {@code @EnableTransactionManagement} and {@code
 * <tx:annotation-driven/>} are responsible for registering the necessary Spring
 * components that power annotation-driven transaction management, such as the
 * TransactionInterceptor and the proxy- or AspectJ-based advice that weave the
 * interceptor into the call stack when {@code JdbcFooRepository}'s {@code @Transactional}
 * methods are invoked.
 *
 * <p>A minor difference between the two examples lies in the naming of the {@code
 * PlatformTransactionManager} bean: In the {@code @Bean} case, the name is
 * <em>"txManager"</em> (per the name of the method); in the XML case, the name is
 * <em>"transactionManager"</em>. The {@code <tx:annotation-driven/>} is hard-wired to
 * look for a bean named "transactionManager" by default, however
 * {@code @EnableTransactionManagement} is more flexible; it will fall back to a by-type
 * lookup for any {@code PlatformTransactionManager} bean in the container. Thus the name
 * can be "txManager", "transactionManager", or "tm": it simply does not matter.
 *
 * <p>For those that wish to establish a more direct relationship between
 * {@code @EnableTransactionManagement} and the exact transaction manager bean to be used,
 * the {@link TransactionManagementConfigurer} callback interface may be implemented -
 * notice the {@code implements} clause and the {@code @Override}-annotated method below:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableTransactionManagement
 * public class AppConfig implements TransactionManagementConfigurer {
 *
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         // configure and return a class having &#064;Transactional methods
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // configure and return the necessary JDBC DataSource
 *     }
 *
 *     &#064;Bean
 *     public PlatformTransactionManager txManager() {
 *         return new DataSourceTransactionManager(dataSource());
 *     }
 *
 *     &#064;Override
 *     public PlatformTransactionManager annotationDrivenTransactionManager() {
 *         return txManager();
 *     }
 * }</pre>
 *
 * This approach may be desirable simply because it is more explicit, or it may be
 * necessary in order to distinguish between two {@code PlatformTransactionManager} beans
 * present in the same container.  As the name suggests, the
 * {@code annotationDrivenTransactionManager()} will be the one used for processing
 * {@code @Transactional} methods. See {@link TransactionManagementConfigurer} Javadoc
 * for further details.
 *
 * <p>The {@link #mode} attribute controls how advice is applied: If the mode is
 * {@link AdviceMode#PROXY} (the default), then the other attributes control the behavior
 * of the proxying. Please note that proxy mode allows for interception of calls through
 * the proxy only; local calls within the same class cannot get intercepted that way.
 *
 * <p>Note that if the {@linkplain #mode} is set to {@link AdviceMode#ASPECTJ}, then the
 * value of the {@link #proxyTargetClass} attribute will be ignored. Note also that in
 * this case the {@code spring-aspects} module JAR must be present on the classpath, with
 * compile-time weaving or load-time weaving applying the aspect to the affected classes.
 * There is no proxy involved in such a scenario; local calls will be intercepted as well.
 * Spring 框架中用于启用注解驱动的事务管理功能的注解，作用如下：
 * 1. 开启 Spring 的注解驱动事务管理功能，使 @Transactional 注解在应用中生效
 * 2. 自动注册管理事务所需的组件，比如TransactionInterceptor、AOP代理等
 *
 * 如何使用：
 * 1. 通常添加到
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see TransactionManagementConfigurer
 * @see TransactionManagementConfigurationSelector
 * @see ProxyTransactionManagementConfiguration
 * @see org.springframework.transaction.aspectj.AspectJTransactionManagementConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created ({@code true}) as
	 * opposed to standard Java interface-based proxies ({@code false}). The default is
	 * {@code false}. <strong>Applicable only if {@link #mode()} is set to
	 * {@link AdviceMode#PROXY}</strong>.
	 * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
	 * Spring-managed beans requiring proxying, not just those marked with
	 * {@code @Transactional}. For example, other beans marked with Spring's
	 * {@code @Async} annotation will be upgraded to subclass proxying at the same
	 * time. This approach has no negative impact in practice unless one is explicitly
	 * expecting one type of proxy vs another, e.g. in tests.
	 *
	 * 表示是否使用CGLIB创建代理对象，默认不使用CGLIB.
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate how transactional advice should be applied.
	 * <p><b>The default is {@link AdviceMode#PROXY}.</b>
	 * Please note that proxy mode allows for interception of calls through the proxy
	 * only. Local calls within the same class cannot get intercepted that way; an
	 * {@link Transactional} annotation on such a method within a local call will be
	 * ignored since Spring's interceptor does not even kick in for such a runtime
	 * scenario. For a more advanced mode of interception, consider switching this to
	 * {@link AdviceMode#ASPECTJ}.
	 *
	 * 默认的代理模式为PROXY. 即：JDK的动态代理
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * Indicate the ordering of the execution of the transaction advisor
	 * when multiple advices are applied at a specific joinpoint.
	 * <p>The default is {@link Ordered#LOWEST_PRECEDENCE}.
	 * 用来指定带有事务功能的方法的执行顺序。例如：如果项目中存在着使用切面完成的数据源的主从切换功能
	 * 事务方法需要在切换完数据源之后再去执行。一般不需要指定，默认就是优先级最低，最后执行
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
