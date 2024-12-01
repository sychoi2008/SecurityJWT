package com.example.springJWT.repository;

import com.example.springJWT.entity.RefreshEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

    //
    Boolean existsByRefresh(String refresh);

    // JPA에서 제공하는 deleteById가 아닌 커스텀된 deleteby 메서드는 @Transactional 처리를 하지 않으면 동작이 되지 않음
    @Transactional
    void deleteByRefresh(String refresh);
}
