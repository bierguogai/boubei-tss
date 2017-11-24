package com.boubei.tssbi.snapshot;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.boubei.tss.PX;
import com.boubei.tss.dm.report.Report;
import com.boubei.tss.dm.report.ReportService;
import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.persistence.ICommonService;
import com.boubei.tss.framework.sso.context.RequestContext;
 
@WebFilter(filterName = "SnapshotFilter",
	urlPatterns = {"/data/json/*", "/api/json/*"}
)
public class SnapshotFilter implements Filter {
    
    private static final Log log = LogFactory.getLog(SnapshotFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("SnapshotFilter init! appCode=" + Config.getAttribute(PX.APPLICATION_CODE));
    }
    
    public void destroy() { }
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

    	request.setCharacterEncoding("utf-8");
    	response.setContentType("text/json; charset=UTF-8");
    	response.setCharacterEncoding("utf-8");
        
        String servletPath = RequestContext.getServletPath((HttpServletRequest)request);
        String report = servletPath.substring( servletPath.lastIndexOf("/") + 1 );
        
        ReportService reportService = (ReportService) Global.getBean("ReportService");
        Object reportId = reportService.getReportId("name", report, Report.TYPE1);
        if(reportId == null) {
        	try {
        		reportId = reportService.getReportId("id", Long.valueOf(report), Report.TYPE1);
        	} catch(Exception e) { }
        }
        
    	Snapshot snapshot = getSnapshot( "queryReport(" + reportId + "," );
    	if(snapshot != null) {
    		response.getWriter().print( snapshot.getIvalue() );
    		return;
    	}

        chain.doFilter(request, response);
    }

	public Snapshot getSnapshot(String key) {
		String hql = "from Snapshot where ikey like '%" +key+ "%' order by id desc";
		ICommonService commonService = Global.getCommonService();
		List<?> data = commonService.getList(hql);
		
		return (Snapshot) (data.isEmpty() ? null : data.get(0));
	}
}
