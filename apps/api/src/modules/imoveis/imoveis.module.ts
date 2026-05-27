import { Module } from '@nestjs/common';
import { ImoveisController } from './imoveis.controller';
import { ImoveisService } from './imoveis.service';
import { ImoveisRepository } from './imoveis.repository';

@Module({
  controllers: [ImoveisController],
  providers: [ImoveisService, ImoveisRepository],
  exports: [ImoveisService],
})
export class ImoveisModule {}
