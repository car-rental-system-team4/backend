using Audit.API.Data;
using Audit.API.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Audit.API.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class AuditsController : ControllerBase
    {
        private readonly AuditContext _context;

        public AuditsController(AuditContext context)
        {
            _context = context;
        }

        [HttpPost]
        public async Task<IActionResult> CreateAuditLog([FromBody] AuditLog log)
        {
            log.Timestamp = DateTime.UtcNow;
            _context.AuditLogs.Add(log);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetAuditLogs), new { id = log.Id }, log);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<AuditLog>>> GetAuditLogs()
        {
            return await _context.AuditLogs.OrderByDescending(x => x.Timestamp).ToListAsync();
        }
    }
}
