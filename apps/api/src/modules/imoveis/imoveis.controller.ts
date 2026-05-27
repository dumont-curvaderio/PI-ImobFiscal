import { Controller, Get, Param } from '@nestjs/common';
import { ApiBearerAuth, ApiOperation, ApiTags } from '@nestjs/swagger';
import { ImoveisService } from './imoveis.service';

// TODO: extrair imobiliaria_id do JWT via @CurrentTenant() decorator
const TENANT_PLACEHOLDER = 'TODO';

@ApiTags('imóveis')
@ApiBearerAuth()
@Controller('imoveis')
export class ImoveisController {
  constructor(private readonly service: ImoveisService) {}

  @Get()
  @ApiOperation({ summary: 'Listar imóveis da imobiliária' })
  findAll() {
    return this.service.findAll(TENANT_PLACEHOLDER);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Buscar imóvel por ID' })
  findById(@Param('id') id: string) {
    return this.service.findById(id, TENANT_PLACEHOLDER);
  }
}
