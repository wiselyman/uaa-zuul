
package com.hfcsbc.repository.support;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;



@NoRepositoryBean
public interface WiselyRepository<E, PK extends Serializable> extends JpaRepository<E, PK> {


		Page<E> queryByExampleWithRange(Example example, List<Range<E>> ranges, Pageable pageable);
	    
}
