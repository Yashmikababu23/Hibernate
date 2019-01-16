package com.fnf.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fnf.model.Bonus;
import com.fnf.model.Employee;
import com.fnf.model.Variable;

@Repository
@Transactional
public class EmployeeDao {
	@PersistenceContext	
	private EntityManager entityManager;
	
	public  List<Employee> getEmployee() {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		  CriteriaQuery<Employee> query = cb.createQuery(Employee.class);
		  Root<Employee> employee = query.from(Employee.class);

		    Join<Employee,Bonus> b = employee.join("bonus", JoinType.LEFT); //left outer join
		    Join<Employee,Variable> v = employee.join("variables", JoinType.LEFT);
		    
		    b.on(
		            cb.and(
		                cb.equal(b.get("payableMonth"),"DEC")		              
		            )   
		        );
		    v.on(
		            cb.and(
		                cb.equal(v.get("payableMonth"),"DEC")		              
		            )   
		        );
		    
		    query.select(employee).distinct(true);
		  TypedQuery<Employee> typedQuery = entityManager.createQuery(query);
		  List<Employee> list = typedQuery.getResultList();
		  return list;

	}


}
