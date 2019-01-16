package com.fnf.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fnf.model.EmployeeHistory;

@Repository
public interface PayrollHisRepository extends CrudRepository<EmployeeHistory, Integer> {

}
