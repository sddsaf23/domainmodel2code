package gen.Cargo;


import gen.annotation.ValueObject;
import gen.annotation.DomainAttribute;
import gen.annotation.DomainBehaviour;

@AggregatePart(aggregateRootType = Itinerary.class)
@ValueObject

public class Leg{

	 
	
	//ֵ����������������
	@DomainAttribute
	private Date loadTime;
	 
	
	//ֵ����������������
	@DomainAttribute
	private Date unloadTime;
	
	
	 
	 
		
		
	//����ֵ����ĺ��ι��캯��
	public Leg(Date loadTime,Date unloadTime){
	 
		this.loadTime=loadTime;
	 
		this.unloadTime=unloadTime;
	
	}
	

	//����ֵ������޲ι��캯��
	public Leg(){
		this.name = Leg;
	}
	
	



	//��ֵ������������Ӧ��getter��setter����
	 
	 
	public Date getLoadTime(){
	
		return this.loadTime;
		
	}
	
	public void setLoadTime(Date cur){
	
		this.loadTime=loadTime;
	
	}
		
	 
	 
	public Date getUnloadTime(){
	
		return this.unloadTime;
		
	}
	
	public void setUnloadTime(Date cur){
	
		this.unloadTime=unloadTime;
	
	}
		
}

