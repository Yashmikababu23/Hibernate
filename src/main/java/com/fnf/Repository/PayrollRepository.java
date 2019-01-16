package com.fnf.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fnf.model.Employee;

@Repository
public interface PayrollRepository extends CrudRepository<Employee, Integer> {
	
	final String  monthFetchQuery = "select distinct e from Employee e "+
	"left outer join fetch e.bonus b left outer join fetch e.variables order by e.employeeId";

	@Query(monthFetchQuery)
	public List<Employee> findEmpByMonth();
	


}
