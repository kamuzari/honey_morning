package com.sf.honeymorning.domain.brief.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.user.entity.User;

public interface BriefRepository extends JpaRepository<Brief, Long> {
	Page<Brief> findByUser(User user, Pageable pageable);
}
