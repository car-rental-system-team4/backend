using Audit.API.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Configure Database
builder.Services.AddDbContext<AuditContext>(options =>
    options.UseSqlite("Data Source=audit.db"));


var app = builder.Build();

// Auto-create database on startup (Fix for Docker 500 Error)
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AuditContext>();
    db.Database.EnsureCreated();
}

// Configure the HTTP request pipeline.
// Configure the HTTP request pipeline.
// Enable Swagger in ALL environments for the demo
app.UseSwagger();
app.UseSwaggerUI();

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
