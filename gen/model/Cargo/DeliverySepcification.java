package gen.Cargo;


import gen.annotation.ValueObject;
import gen.annotation.DomainAttribute;
import gen.annotation.DomainBehaviour;

@AggregatePart(aggregateRootType = Cargo.class)
@ValueObject

public class DeliverySepcification{

	 
	
	//ֵ����������������
	@DomainAttribute
	private Date arriveDeadline;
	
	
	 
		
		
	//����ֵ����ĺ��ι��캯��
	public DeliverySepcification(Date arriveDeadline){
	 
		this.arriveDeadline=arriveDeadline;
	
	}
	

	//����ֵ������޲ι��캯��
	public DeliverySepcification(){
		this.name = DeliverySepcification;
	}
	
	



	//��ֵ������������Ӧ��getter��setter����
	 
	 
	public Date getArriveDeadline(){
	
		return this.arriveDeadline;
		
	}
	
	public void setArriveDeadline(Date cur){
	
		this.arriveDeadline=arriveDeadline;
	
	}
		
}

