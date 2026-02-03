using Audit.API.Models;
using Microsoft.EntityFrameworkCore;

namespace Audit.API.Data
{
    public class AuditContext : DbContext
    {
        public AuditContext(DbContextOptions<AuditContext> options) : base(options) { }

        public DbSet<AuditLog> AuditLogs { get; set; }
    }
}
